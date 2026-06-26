package com.proxiad.payment.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxiad.payment.entity.PaymentEntity;
import com.proxiad.payment.enums.PaymentStatus;
import com.proxiad.payment.enums.ResourceType;
import com.proxiad.payment.event.EventPublisher;
import com.proxiad.payment.event.PaymentStatusEvent;
import com.proxiad.payment.repository.PaymentRepository;
import com.proxiad.payment.service.PaymentService;
import com.proxiad.payment.service.PayPalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookHandler {

    private final PayPalService payPalService;
    private final PaymentService paymentService;
    private final EventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private final PaymentRepository paymentRepository;

    public void handleWebhook(String payload, String transmissionId, String certUrl,
                              String authAlgo, String transmissionSig, String transmissionTime) {

        try {
            // Parse webhook payload first
            WebhookEvent webhookEvent = parseWebhookPayload(payload);

            // Verify webhook authenticity
            if (!payPalService.verifyWebhook(payload, transmissionId, certUrl, authAlgo, transmissionSig, transmissionTime)) {
                log.error("Webhook verification failed for transmission ID: {}", transmissionId);
                throw new WebhookVerificationException("Webhook verification failed");
            }

            log.info("Processing verified webhook event type: {}, resource ID: {}",
                    webhookEvent.getEventType(), webhookEvent.getResourceId());

            // Process the webhook event
            processWebhookEvent(webhookEvent);

        } catch (WebhookVerificationException e) {
            log.error("Webhook verification failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
            throw new WebhookProcessingException("Failed to process webhook", e);
        }
    }

    private WebhookEvent parseWebhookPayload(String payload) {
        try {
            Map<String, Object> event = objectMapper.readValue(payload, Map.class);
            Map<String, Object> resource = (Map<String, Object>) event.get("resource");

            return WebhookEvent.builder()
                    .eventType((String) event.get("event_type"))
                    .resourceType((String) event.get("resource_type"))
                    .resourceId((String) resource.get("id"))
                    .resource(resource)
                    .build();

        } catch (Exception e) {
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
        if (payment.getStatus().equals(PaymentStatus.AUTHORIZED)) {
            paymentService.updateStatus(
                    ResourceType.fromValue(webhookEvent.getResourceType()),
                    webhookEvent.getResourceId(),
                    PaymentStatus.CAPTURED
            );
        }else {
            log.info("Payment {} already in status {}, ignoring webhook", payment.getId(), payment.getStatus());
        }
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
            eventPublisher.publish(new PaymentStatusEvent(
                    payment.getId(),
                    payment.getOrderId(),
                    payment.getStatus(),
                    LocalDate.now()
            ));
        } catch (Exception e) {
            log.error("Failed to publish payment event for payment ID: {}", payment.getId(), e);
        }
    }
}