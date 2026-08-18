package com.abdelkhalek.storehub.payment.webhook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.abdelkhalek.storehub.payment.dto.PayPalWebhookPayload;
import com.abdelkhalek.storehub.payment.entity.PaymentEntity;
import com.abdelkhalek.storehub.payment.entity.ProcessedWebhookEventEntity;
import com.abdelkhalek.storehub.payment.enums.PaymentStatus;
import com.abdelkhalek.storehub.payment.enums.ResourceType;
import com.abdelkhalek.storehub.payment.EventPublisher;
import com.abdelkhalek.storehub.payment.event.PaymentStatusUpdateEvent;
import com.abdelkhalek.storehub.payment.exception.WebhookProcessingException;
import com.abdelkhalek.storehub.payment.exception.WebhookVerificationException;
import com.abdelkhalek.storehub.payment.repository.ProcessedWebhookEventRepository;
import com.abdelkhalek.storehub.payment.service.PaymentService;
import com.abdelkhalek.storehub.payment.service.PayPalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookHandler {

    private final PayPalService payPalService;
    private final PaymentService paymentService;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final ProcessedWebhookEventRepository processedWebhookEventRepository;

    public void handleWebhook(String payload, String transmissionId, String certUrl,
                              String authAlgo, String transmissionSig, String transmissionTime) {
        WebhookEvent webhookEvent = parseWebhookPayload(payload);

        if (!payPalService.verifyWebhook(payload, transmissionId, certUrl, authAlgo, transmissionSig, transmissionTime)) {
            throw new WebhookVerificationException("Webhook verification failed");
        }

        if (!tryMarkProcessed(webhookEvent.getEventId())) {
            log.info("Duplicate webhook event {}, skipping", webhookEvent.getEventId());
            return;
        }

        processWebhookEvent(webhookEvent);
    }

    private boolean tryMarkProcessed(String eventId) {
        Optional<ProcessedWebhookEventEntity> pwe = processedWebhookEventRepository.findByEventId(eventId);
        if (pwe.isPresent()) {
            log.debug("The event is already processed {}", eventId);
            return false; // already processed, duplicate delivery
        } else {
            log.debug("Marking the event as processed {}", eventId);
            processedWebhookEventRepository.save(
                    ProcessedWebhookEventEntity.builder()
                            .eventId(eventId)
                            .processedAt(LocalDateTime.now())
                            .build()
            );
            return true; // first time seeing this event
        }
    }

    private WebhookEvent parseWebhookPayload(String payload) {
        try {
            PayPalWebhookPayload event = objectMapper.readValue(payload, PayPalWebhookPayload.class);
            String resourceId = event.resource().path("id").asText(null);

            log.debug("Webhook event: {}", event);

            return WebhookEvent.builder()
                    .eventType(event.eventType())
                    .resourceType(event.resourceType())
                    .resourceId(resourceId)
                    .eventId(event.id())
                    .resource(event.resource())
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Failed to parse webhook payload: {}", e.getMessage());
            throw new WebhookProcessingException("Invalid webhook payload format", e);
        }
    }

    private void processWebhookEvent(WebhookEvent webhookEvent) {
        switch (webhookEvent.getEventType()) {
            case "CHECKOUT.ORDER.APPROVED":
                handleOrderApproved(webhookEvent);
                break;
            case "PAYMENT.AUTHORIZATION.CREATED":
                handleAuthorizationCreated(webhookEvent);
                break;
            case "PAYMENT.CAPTURE.COMPLETED":
                handleCaptureCompleted(webhookEvent);
                break;
            case "PAYMENT.CAPTURE.REFUNDED":
                handleCaptureRefunded(webhookEvent);
                break;
            case "PAYMENT.AUTHORIZATION.VOIDED":
                handleAuthorizationVoided(webhookEvent);
                break;
            default:
                log.warn("Unhandled webhook event type: {}", webhookEvent.getEventType());
        }
    }

    private void handleOrderApproved(WebhookEvent webhookEvent) {
        log.debug("Processing order approved event type: {}", webhookEvent.getEventType());
        PaymentEntity payment = paymentService.updateStatus(
                ResourceType.fromValue(webhookEvent.getResourceType()),
                webhookEvent.getResourceId(),
                PaymentStatus.APPROVED
        );

        publishPaymentEvent(payment);

        // Automatically authorize the order
        try {
            paymentService.setAuthorizationId(
                    ResourceType.ID,
                    payment.getId().toString(),
                    payPalService.authorizeOrder(payment.getPaymentOrderId()).getAuthorizationId()
            );
        } catch (Exception e) {
            log.error("Failed to auto-authorize order: {}", payment.getPaymentOrderId(), e);
        }
    }

    private void handleAuthorizationCreated(WebhookEvent webhookEvent) {
        log.debug("Processing authorization created event type: {}", webhookEvent.getEventType());
        PaymentEntity payment = paymentService.updateStatus(
                ResourceType.fromValue(webhookEvent.getResourceType()),
                webhookEvent.getResourceId(),
                PaymentStatus.AUTHORIZED
        );

        publishPaymentEvent(payment);
    }

    private void handleCaptureCompleted(WebhookEvent webhookEvent) {
        log.debug("Processing capture completed event type: {}", webhookEvent.getEventType());
        PaymentEntity payment = paymentService.findPaymentByResource(ResourceType.fromValue(webhookEvent.getResourceType()), webhookEvent.getResourceId());
        // This may indicate a bug in setting the payment state to CAPTURE_PENDING
        if (!payment.getStatus().equals(PaymentStatus.CAPTURE_PENDING)) {
            log.warn("Abnormal payment {} transition from {} to {}, expected to be from {} to {}",
                    payment.getId(), payment.getStatus(), PaymentStatus.CAPTURED,
                    PaymentStatus.CAPTURE_PENDING, PaymentStatus.CAPTURED);
        }
        paymentService.updateStatus(
                ResourceType.fromValue(webhookEvent.getResourceType()),
                webhookEvent.getResourceId(),
                PaymentStatus.CAPTURED
        );
        publishPaymentEvent(payment);

    }

    private void handleCaptureRefunded(WebhookEvent webhookEvent) {
        log.debug("processing capture refunded event type: {}", webhookEvent.getEventType());
        PaymentEntity payment = paymentService.updateStatus(
                ResourceType.fromValue(webhookEvent.getResourceType()),
                webhookEvent.getResourceId(),
                PaymentStatus.REFUNDED
        );

        publishPaymentEvent(payment);
    }

    private void handleAuthorizationVoided(WebhookEvent webhookEvent) {
        log.debug("Processing authorization voided event type: {}", webhookEvent.getEventType());


        PaymentEntity payment = paymentService.updateStatus(
                ResourceType.fromValue(webhookEvent.getResourceType()),
                webhookEvent.getResourceId(),
                PaymentStatus.CANCELLED
        );

        publishPaymentEvent(payment);
    }

    private void publishPaymentEvent(PaymentEntity payment) {
        try {
            eventPublisher.publish(new PaymentStatusUpdateEvent(
                            payment.getOrderId(),
                            payment.getId(),
                            PaymentStatusUpdateEvent
                                    .PaymentStatus.valueOf(payment.getStatus().name()),
                            Instant.now()
                    )
            );
        } catch (Exception e) {
            log.error("Failed to publish payment event for payment ID: {}", payment.getId(), e);
        }
    }
}