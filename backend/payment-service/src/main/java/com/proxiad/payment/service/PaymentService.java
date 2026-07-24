package com.proxiad.payment.service;

import com.proxiad.payment.controller.PaymentFilter;
import com.proxiad.payment.dto.AuthorizePaypalOrderResponse;
import com.proxiad.payment.dto.CreatePaypalOrderResponse;
import com.proxiad.payment.dto.PaymentResponse;
import com.proxiad.payment.entity.MoneyEntity;
import com.proxiad.payment.entity.PaymentAuditEntity;
import com.proxiad.payment.entity.PaymentEntity;
import com.proxiad.payment.enums.PaymentStatus;
import com.proxiad.payment.enums.ResourceType;
import com.proxiad.payment.exception.PaymentNotFoundException;
import com.proxiad.payment.exception.PaymentServiceException;
import com.proxiad.payment.mapper.PaymentMapper;
import com.proxiad.payment.repository.PaymentAuditRepository;
import com.proxiad.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Currency;
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

    public PaymentEntity getById(String paymentId) {
        return findPaymentByResource(ResourceType.ID, paymentId);
    }

    public PaymentEntity getByAuthorizationId(String authorizationId) {
        return findPaymentByResource(ResourceType.AUTHORIZATION, authorizationId);
    }

    public PaymentEntity getByCaptureId(String captureId) {
        return findPaymentByResource(ResourceType.CAPTURE, captureId);
    }

    public PaymentEntity getByPaypalOrderId(String paypalOrderId) {
        return findPaymentByResource(ResourceType.CHECKOUT_ORDER, paypalOrderId);
    }

    public PaymentEntity getByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(
                        "Payment not found by order ID: " + orderId));
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
            CreatePaypalOrderResponse paypalOrder = payPalService.createOrder(amount);

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
            // Race: another request created it first, return the existing one
            return paymentRepository.findByOrderId(orderId)
                    .map((payment -> {
                        log.info("Payment already exists for order: {}, returning existing payment: {}",
                                payment.getOrderId(), payment.getId());
                        return paymentMapper.fromEntityToResponse(
                                payment,
                                "Payment already exists for this order");
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

            PaymentEntity payment = findPaymentByResource(ResourceType.AUTHORIZATION, authorizationId);

            // Call PayPal API first
            String paypalStatus = payPalService.voidAuthorizedPayment(authorizationId);
            log.debug("PayPal void response status: {}", paypalStatus);

            // Update payment status
//            PaymentEntity payment = updateStatus(
//                    ResourceType.AUTHORIZATION,
//                    authorizationId,
//                    PaymentStatus.CANCELLED
//            );

            return paymentMapper.fromEntityToResponse(payment, "Authorization voided successfully, status updates when webhook is processed");
        } catch (PaymentNotFoundException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            log.error("Failed to void authorization: {}", authorizationId, e);
            throw new PaymentServiceException("Failed to void authorization", e);
        }
    }

    @Transactional
    public PaymentResponse refundCapture(String captureId) {
        try {
            log.info("Refunding capture: {}", captureId);

            // Call PayPal API first
            String refundId = payPalService.refundCapture(captureId);

            // Update payment status
//            PaymentEntity payment = updateStatus(
//                    ResourceType.CAPTURE,
//                    captureId,
//                    PaymentStatus.REFUNDED
//            );

            PaymentEntity payment = setRefundId(ResourceType.CAPTURE, captureId, refundId);

            return paymentMapper.fromEntityToResponse(payment, "refund request accepted, processing...");

        } catch (Exception e) {
            log.error("Failed to refund capture: {}", captureId, e);
            throw new PaymentServiceException("Failed to refund capture", e);
        }
    }

    @Transactional
    public PaymentResponse captureAuthorization(String authorizationId) {
        try {
            log.info("Capturing authorization: {}", authorizationId);

            // Call PayPal API first
            String captureId = payPalService.captureAuthorizedPayment(authorizationId);
            log.debug("PayPal capture id: {}", captureId);

            // Update payment status
//            PaymentEntity payment = updateStatus(
//                    ResourceType.AUTHORIZATION,
//                    authorizationId,
//                    PaymentStatus.CAPTURED
//            );

            // Set the capture id
            PaymentEntity payment = setCaptureId(ResourceType.AUTHORIZATION, authorizationId, captureId);

            return paymentMapper.fromEntityToResponse(payment,
                    "Authorization captured successfully, status will get updated when the webhook is processed");

        } catch (Exception e) {
            log.error("Failed to capture authorization: {}", authorizationId, e);
            throw new PaymentServiceException("Failed to capture authorization", e);
        }
    }

    @Transactional
    public PaymentResponse authorizePaypalOrder(String paypalOrderId) {
        try {
            log.info("Authorizing PayPal order: {}", paypalOrderId);

            // Call PayPal API first
            AuthorizePaypalOrderResponse authorizationResponse = payPalService.authorizeOrder(paypalOrderId);
            log.debug("PayPal authorization response: {}", authorizationResponse);

            log.info("PayPal authorize response: {}", authorizationResponse);

            // Update payment status
            PaymentEntity payment = updateStatus(
                    ResourceType.CHECKOUT_ORDER,
                    paypalOrderId,
                    PaymentStatus.AUTHORIZED
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
    public PaymentEntity updateStatus(ResourceType resourceType,
                                      String resourceId, PaymentStatus newStatus) {
        try {
            log.debug("Updating payment status - ResourceType: {}, ResourceId: {}, NewStatus: {}",
                    resourceType, resourceId, newStatus);

            PaymentEntity paymentEntity = findPaymentByResource(resourceType, resourceId);
            PaymentStatus oldStatus = paymentEntity.getStatus();

            // Update status and last modified time
            paymentEntity.setStatus(newStatus);
            paymentEntity.setUpdatedAt(LocalDateTime.now());

            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);

            // Create audit record
            createAuditRecord(paymentEntity.getId(), oldStatus, newStatus,
                    "Status updated via " + resourceType + " resource");

            log.debug("Payment status updated successfully - PaymentId: {}, OldStatus: {}, NewStatus: {}",
                    paymentEntity.getId(), oldStatus, newStatus);

            return savedPayment;

        } catch (PaymentNotFoundException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            log.error("Failed to update payment status - ResourceType: {}, ResourceId: {}, NewStatus: {}",
                    resourceType, resourceId, newStatus, e);
            throw new PaymentServiceException("Failed to update payment status", e);
        }
    }

    @Transactional
    public PaymentEntity setAuthorizationId(ResourceType resourceType, String resourceId, String authorizationId) {
        try {
            log.debug("Setting payment authorization id - ResourceType: {}, ResourceId: {}, AuthorizationId: {}",
                    resourceType, resourceId, authorizationId);

            PaymentEntity paymentEntity = findPaymentByResource(resourceType, resourceId);

            paymentEntity.setAuthorizationId(authorizationId);
            paymentEntity.setUpdatedAt(LocalDateTime.now());

            PaymentEntity savedPayment = paymentRepository.save(paymentEntity);


            log.debug("Payment authorization id set successfully - PaymentId: {}, authorizationId: {}",
                    paymentEntity.getId(), paymentEntity.getAuthorizationId());

            return savedPayment;

        } catch (PaymentNotFoundException e) {
            throw e; // Re-throw as is
        } catch (Exception e) {
            log.error("Failed to set payment authorization id - ResourceType: {}, ResourceId: {}, AuthorizationId: {}",
                    resourceType, resourceId, authorizationId, e);
            throw new PaymentServiceException("Failed to set authorization id", e);
        }
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
        switch (resourceType) {
            case CHECKOUT_ORDER:
                return paymentRepository.findByPaymentOrderId(resourceId)
                        .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment not found by payment order ID: " + resourceId));

            case AUTHORIZATION:
                return paymentRepository.findByAuthorizationId(resourceId)
                        .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment not found by authorization ID: " + resourceId));

            case CAPTURE:
                return paymentRepository.findByCaptureId(resourceId)
                        .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment not found by capture ID: " + resourceId));
            case ID:
                return paymentRepository.findById(UUID.fromString(resourceId))
                        .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment not found by Payment ID: " + resourceId));
            case REFUND:
                return paymentRepository.findByRefundId(resourceId)
                        .orElseThrow(() -> new PaymentNotFoundException(
                                "Payment not found by Refund ID: " + resourceId));
            default:
                throw new IllegalArgumentException(
                        "Unsupported resource type: " + resourceType +
                                ". Supported types are: " + Arrays.toString(ResourceType.values()));
        }
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