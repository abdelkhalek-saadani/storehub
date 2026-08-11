package com.abdelkhalek.storehub.payment.service;

import com.abdelkhalek.storehub.payment.model.PaymentFilter;
import com.abdelkhalek.storehub.payment.dto.AuthorizePaypalOrderResponse;
import com.abdelkhalek.storehub.payment.dto.CreatePaypalOrderResponse;
import com.abdelkhalek.storehub.payment.dto.PaymentResponse;
import com.abdelkhalek.storehub.payment.entity.PaymentAuditEntity;
import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import com.abdelkhalek.storehub.payment.enums.ResourceType;
import com.abdelkhalek.storehub.payment.exception.PayPalApiException;
import com.abdelkhalek.storehub.payment.exception.PaymentNotFoundException;
import com.abdelkhalek.storehub.payment.exception.PaymentServiceException;
import com.abdelkhalek.storehub.payment.mapper.PaymentMapper;
import com.abdelkhalek.storehub.payment.repository.PaymentAuditRepository;
import com.abdelkhalek.storehub.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAuditRepository paymentAuditRepository;
    private final PayPalService payPalService;
    private final PaymentMapper paymentMapper;

    public PaymentEntity getById(UUID paymentId) {
        return findPaymentByResource(ResourceType.ID, paymentId.toString());
    }

    public PaymentEntity getByAuthorizationId(String authorizationId) {
        return findPaymentByResource(ResourceType.AUTHORIZATION, authorizationId);
    }

    public PaymentEntity getByCaptureId(String captureId) {
        return findPaymentByResource(ResourceType.CAPTURE, captureId);
    }





    public PaymentEntity getByCustomerId(UUID customerId) {
        return paymentRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found by customer ID: " + customerId));
    }

    public Page<PaymentEntity> getPayments(PaymentFilter filter, Pageable pageable) {
        return paymentRepository.findWithFilters(filter.getStatus(), filter.getStartDate(), filter.getEndDate(), pageable);
    }


    @Transactional
    public PaymentResponse createPayment(UUID orderId, UUID customerId, BigDecimal amount) {
        Optional<PaymentEntity> existing = paymentRepository.findByOrderId(orderId);
        if (existing.isPresent()) {
            log.warn("Payment already exists for order: {}, returning existing payment: {}",
                    existing.get().getOrderId(), existing.get().getId());
            return paymentMapper.fromEntityToResponse(
                    existing.get(),
                    "Payment already exists for this order");
        }

        try {
            CreatePaypalOrderResponse paypalOrder = payPalService.createOrder(amount,orderId);

            PaymentEntity paymentEntity = PaymentEntity.builder()
                    .orderId(orderId)
                    .customerId(customerId)
                    .amount(amount)
                    .approvalUrl(paypalOrder.getApprovalUrl())
                    .paymentOrderId(paypalOrder.getPaymentOrderId())
                    .status(PaymentStatus.CREATED)
                    .createdAt(LocalDateTime.now())
                    .build();

            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);
            createAuditRecord(savedPayment.getId(), null, savedPayment.getStatus(), "Payment created");
            return paymentMapper.fromEntityToResponse(savedPayment, "Payment created successfully");

        } catch (DataIntegrityViolationException e) {
            // Race: another request created it first, return the existing one(rare case)
            return paymentRepository.findByOrderId(orderId)
                    .map((payment -> {
                        log.info("Payment already exists for order: {}, returning existing " +
                                        "payment: {} ( TOCTOU race)",
                                payment.getOrderId(), payment.getId());
                        return paymentMapper.fromEntityToResponse(
                                payment,
                                "Payment already exists for this order due to another request won" +
                                        " the race");
                    }))
                    .orElseThrow(() -> new PaymentServiceException("Failed to create payment", e));
        } catch (Exception e) {
            log.error("Failed to create payment for order: {}", orderId, e);
            throw new PaymentServiceException("Failed to create payment", e);
        }
    }


    @Transactional
    public PaymentResponse voidAuthorization(String authorizationId) {
        try {
            log.info("Voiding authorization: {}", authorizationId);

            String paypalStatus = payPalService.voidAuthorizedPayment(authorizationId);
            log.debug("PayPal void response status: {}", paypalStatus);

            PaymentEntity payment = updateStatus(ResourceType.AUTHORIZATION, authorizationId,
                    PaymentStatus.CANCEL_PENDING);

            return paymentMapper.fromEntityToResponse(payment, "Void requested, status updates when webhook is processed");
        } catch (PayPalApiException e) {
            log.error("Failed to void authorization: {}", authorizationId, e);
            throw new PaymentServiceException("Failed to void authorization", e);
        }
    }

    @Transactional
    public PaymentResponse refundCapture(String captureId) {
        try {
            log.info("Refunding capture: {}", captureId);

            String refundId = payPalService.refundCapture(captureId);

            PaymentEntity payment = setRefundId(ResourceType.CAPTURE, captureId, refundId);
            payment = updateStatus(payment, PaymentStatus.REFUND_PENDING);

            return paymentMapper.fromEntityToResponse(payment, "Refund request accepted, processing...");
        } catch (PaymentNotFoundException e) {
            throw e;
        } catch (PayPalApiException e) {
            log.error("Failed to refund capture: {}", captureId, e);
            throw new PaymentServiceException("Failed to refund capture", e);
        }
    }

    @Transactional
    public PaymentResponse captureAuthorization(String authorizationId) {
        try {
            log.info("Capturing authorization: {}", authorizationId);

            String captureId = payPalService.captureAuthorizedPayment(authorizationId);
            log.debug("PayPal capture id: {}", captureId);

            PaymentEntity payment = setCaptureId(ResourceType.AUTHORIZATION, authorizationId, captureId);
            payment = updateStatus(payment, PaymentStatus.CAPTURE_PENDING);

            return paymentMapper.fromEntityToResponse(payment,
                    "Capture requested, status will get updated when the webhook is processed");
        } catch (PaymentNotFoundException e) {
            throw e;
        } catch (PayPalApiException e) {
            log.error("Failed to capture authorization: {}", authorizationId, e);
            throw new PaymentServiceException("Failed to capture authorization", e);
        }
    }

    @Transactional
    public PaymentResponse authorizePaypalOrder(String paypalOrderId) {
        try {
            log.debug("Authorizing PayPal order: {}", paypalOrderId);

            // Call PayPal API first
            AuthorizePaypalOrderResponse authorizationResponse = payPalService.authorizeOrder(paypalOrderId);
            log.debug("PayPal authorization response: {}", authorizationResponse);

            // Update payment status
            PaymentEntity payment = updateStatus(
                    ResourceType.CHECKOUT_ORDER,
                    paypalOrderId,
                    PaymentStatus.AUTHORIZE_PENDING
            );

            payment.setAuthorizationId(authorizationResponse.getAuthorizationId());
            paymentRepository.save(payment);

            return paymentMapper.fromEntityToResponse(payment, "Order authorized successfully");

        } catch (Exception e) {
            log.error("Failed to authorize order: {}", paypalOrderId, e);
            throw new PaymentServiceException("Failed to authorize order", e);
        }
    }


    @Transactional
    public PaymentEntity updateStatus(ResourceType resourceType, String resourceId, PaymentStatus newStatus) {
        PaymentEntity payment = findPaymentByResource(resourceType, resourceId);
        return updateStatus(payment, newStatus);
    }

    @Transactional
    public PaymentEntity updateStatus(PaymentEntity paymentEntity, PaymentStatus newStatus) {
        log.debug("Updating payment status - PaymentId: {}, NewStatus: {}", paymentEntity.getId(), newStatus);

        PaymentStatus oldStatus = paymentEntity.getStatus();

        if (newStatus.equals(PaymentStatus.CANCELLED)
                && (!oldStatus.equals(PaymentStatus.CANCEL_PENDING))) {
            log.warn("Payment Status Moving from {} to {}, Expected to be moving from " +
                    "{}_PENDING to {}", oldStatus, newStatus, newStatus, newStatus);
        }

        paymentEntity.setStatus(newStatus);
        paymentEntity.setUpdatedAt(LocalDateTime.now());

        PaymentEntity savedPayment = paymentRepository.save(paymentEntity);

        createAuditRecord(paymentEntity.getId(), oldStatus, newStatus, "Status updated");

        log.debug("Payment status updated successfully - PaymentId: {}, OldStatus: {}, NewStatus: {}",
                paymentEntity.getId(), oldStatus, newStatus);

        return savedPayment;
    }


    public PaymentEntity setAuthorizationId(PaymentEntity payment, String authorizationId) {
        log.debug("Setting payment authorization id {}", authorizationId);
        payment.setAuthorizationId(authorizationId);
        payment.setUpdatedAt(LocalDateTime.now());

        PaymentEntity savedPayment = paymentRepository.save(payment);

        log.debug("Payment authorization id set successfully - PaymentId: {}, authorizationId: {}",
                payment.getId(), payment.getAuthorizationId());

        return savedPayment;
    }

    @Transactional
    public PaymentEntity setAuthorizationId(ResourceType resourceType, String resourceId, String authorizationId) {
        PaymentEntity paymentEntity = findPaymentByResource(resourceType, resourceId);
        return setAuthorizationId(paymentEntity, authorizationId);
    }

    public PaymentEntity setRefundId(ResourceType resourceType, String resourceId, String refundId) {
        try {
            log.debug("Setting payment refund id - ResourceType: {}, ResourceId: {}, RefundId: {}",
                    resourceType, resourceId, refundId);

            PaymentEntity paymentEntity = findPaymentByResource(resourceType, resourceId);

            paymentEntity.setRefundId(refundId);
            paymentEntity.setUpdatedAt(LocalDateTime.now());

            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);


            log.debug("Payment refund id set successfully - PaymentId: {}, RefundId: {}",
                    paymentEntity.getId(), paymentEntity.getAuthorizationId());

            return savedPayment;

        } catch (PaymentNotFoundException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            log.error("Failed to set payment refund id - ResourceType: {}, ResourceId: {}, RefundId: {}",
                    resourceType, resourceId, refundId, e);
            throw new PaymentServiceException("Failed to set refund id", e);
        }
    }

    public PaymentEntity setCaptureId(ResourceType resourceType, String resourceId, String captureId) {
        try {
            log.debug("Setting payment capture id - ResourceType: {}, ResourceId: {}, CaptureId: {}",
                    resourceType, resourceId, captureId);

            PaymentEntity paymentEntity = findPaymentByResource(resourceType, resourceId);

            paymentEntity.setCaptureId(captureId);
            paymentEntity.setUpdatedAt(LocalDateTime.now());

            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);


            log.debug("Payment capture id set successfully - PaymentId: {}, CaptureId: {}",
                    paymentEntity.getId(), paymentEntity.getCaptureId());

            return savedPayment;

        } catch (PaymentNotFoundException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            log.error("Failed to set payment capture id - ResourceType: {}, ResourceId: {}, CaptureId: {}",
                    resourceType, resourceId, captureId, e);
            throw new PaymentServiceException("Failed to set capture id", e);
        }
    }

    public PaymentEntity findPaymentByResource(ResourceType resourceType, String resourceId) {
        return switch (resourceType) {
            case CHECKOUT_ORDER -> paymentRepository.findByPaymentOrderId(resourceId)
                    .orElseThrow(() -> new PaymentNotFoundException(
                            "Payment not found by payment order ID: " + resourceId));
            case AUTHORIZATION -> paymentRepository.findByAuthorizationId(resourceId)
                    .orElseThrow(() -> new PaymentNotFoundException(
                            "Payment not found by authorization ID: " + resourceId));
            case CAPTURE -> paymentRepository.findByCaptureId(resourceId)
                    .orElseThrow(() -> new PaymentNotFoundException(
                            "Payment not found by capture ID: " + resourceId));
            case ID -> paymentRepository.findById(UUID.fromString(resourceId))
                    .orElseThrow(() -> new PaymentNotFoundException(
                            "Payment not found by Payment ID: " + resourceId));
            case REFUND -> paymentRepository.findByRefundId(resourceId)
                    .orElseThrow(() -> new PaymentNotFoundException(
                            "Payment not found by Refund ID: " + resourceId));
        };
    }

    private void createAuditRecord(UUID paymentId, PaymentStatus oldStatus, PaymentStatus newStatus, String description) {
        try {
            PaymentAuditEntity auditEntity = PaymentAuditEntity.builder()
                    .paymentId(paymentId)
                    .oldStatus(oldStatus)
                    .newStatus(newStatus)
                    .reason(description)
                    .timestamp(LocalDateTime.now())
                    .createdBy("SYSTEM")
                    .build();

            paymentAuditRepository.save(auditEntity);

        } catch (Exception e) {
            log.error("Failed to create audit record for payment: {}", paymentId, e);
            // Don't throw exception here as audit failure shouldn't break main flow
        }
    }

    // Query methods for retrieving payments
    public PaymentEntity getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    public PaymentEntity getPaymentByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order ID: " + orderId));
    }
}