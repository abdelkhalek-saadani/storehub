package com.abdelkhalek.storehub.payment.service;

import com.abdelkhalek.storehub.payment.dto.paypal.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.abdelkhalek.storehub.payment.common.config.PaypalProperties;
import com.abdelkhalek.storehub.payment.dto.AuthorizePaypalOrderResponse;
import com.abdelkhalek.storehub.payment.dto.CreatePaypalOrderResponse;
import com.abdelkhalek.storehub.payment.dto.PayPalOrderResponse;
import com.abdelkhalek.storehub.payment.exception.PayPalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class PayPalService {


    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final PaypalProperties props;


    public CreatePaypalOrderResponse createOrder(BigDecimal amount, UUID orderId) {
        PayPalOrderRequest orderRequest = createOrderPayload(amount, orderId);

        PayPalOrderResponse response = restClient.post()
                .uri("/v2/checkout/orders")
                .header("Content-Type", "application/json")
                .body(orderRequest)
                .retrieve()
                .body(PayPalOrderResponse.class);

        log.debug("PayPal order creation response: {}", response);
        if (response == null) {
            throw new PayPalApiException("PayPal returned an empty response body from " +
                    "/v2/checkout/orders with request body: " + orderRequest);
        }
        return extractOrderResponse(response);
    }

    public String captureAuthorizedPayment(String authorizationId) {


        PayPalCaptureResponse response = restClient.post()
                .uri("/v2/payments/authorizations/{authorizationId}/capture", authorizationId)
                .header("Content-Type", "application/json")
                .retrieve()
                .body(PayPalCaptureResponse.class);

        log.debug("PayPal capture response: {}", response);
        if (response == null) {
            throw new PayPalApiException(("PayPal returned an empty response body on " +
                    "/v2/payments/authorizations/%s/capture").formatted(authorizationId));
        }
        return response.id();

    }

    public String voidAuthorizedPayment(String authorizationId) {


        PayPalVoidResponse response = restClient.post()
                .uri("/v2/payments/authorizations/{authorizationId}/void", authorizationId)
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .retrieve()
                .body(PayPalVoidResponse.class);

        log.debug("PayPal void response: {}", response);
        if (response == null) {
            log.error("Failed to void authorized payment: {}", authorizationId);
            throw new PayPalApiException(("PayPal returned an empty response body on " +
                    "/v2/payments/authorizations/%s/void").formatted(authorizationId));
        }

        // status from the void endpoint always set to VOIDED, see https://developer.paypal.com/api/payments/v2/authorizations-void
        return response.status();


    }

    public String refundCapture(String captureId) {
        PayPalRefundResponse response = restClient.post()
                .uri("/v2/payments/captures/{captureId}/refund", captureId)
                .header("Content-Type", "application/json")
                .retrieve()
                .body(PayPalRefundResponse.class);

        log.debug("PayPal refund response: {}", response);
        if (response == null) {
            throw new PayPalApiException(("PayPal returned an empty response body on " +
                    "/v2/payments/captures/%s/refund").formatted(captureId));
        }
        return response.id();


    }

    public AuthorizePaypalOrderResponse authorizeOrder(String orderId) {
        PayPalAuthorizeResponse response = restClient.post()
                .uri("/v2/checkout/orders/{orderId}/authorize", orderId)
                .header("Content-Type", "application/json")
                .retrieve()
                .body(PayPalAuthorizeResponse.class);

        if (response == null) {
            throw new PayPalApiException("PayPal returned an empty authorize response");
        }

        log.debug("PayPal authorize response: {}", response);
        return extractAuthorizationResponse(response);
    }

    public boolean verifyWebhook(String payload, String transmissionId, String certUrl,
                                 String authAlgo, String transmissionSig, String transmissionTime) {
        JsonNode webhookEvent;
        try {
            webhookEvent = objectMapper.readTree(payload);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse webhook payload as JSON", e);
            return false;
        }

        log.debug("sanity check webhook event: {}", webhookEvent);

        PayPalWebhookVerificationRequest verificationRequest = new PayPalWebhookVerificationRequest(
                transmissionId, certUrl, authAlgo, transmissionSig, transmissionTime,
                props.webhook().id(),
                webhookEvent
        );

        /*PayPalWebhookVerificationResponse response;
        try {
            response = restClient.post()
                    .uri("/v1/notifications/verify-webhook-signature")
                    .header("Content-Type", "application/json")
                    .body(verificationRequest)
                    .retrieve()
                    .body(PayPalWebhookVerificationResponse.class);
        } catch (RestClientException e) {
            log.error("PayPal webhook verification call failed", e);
            return false;
        }*/

        // TEST
        String rawBody = restClient.post()
                .uri("/v1/notifications/verify-webhook-signature")
                .header("Content-Type", "application/json")
                .body(verificationRequest)
                .retrieve()
                .body(String.class);

        log.info("PayPal verification raw response: {}", rawBody);

        PayPalWebhookVerificationResponse response;
        try {
            response = objectMapper.readValue(rawBody, PayPalWebhookVerificationResponse.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse verification response", e);
            return false;
        }
        // END TEST

        if (response == null) {
            log.error("PayPal webhook verification returned an empty response");
            return false;
        }

        boolean isValid = "SUCCESS".equals(response.verificationStatus());
        log.debug("Webhook verification result: {}", isValid);
        return isValid;
    }


    private PayPalOrderRequest createOrderPayload(BigDecimal amount, UUID orderId) {
        log.debug("the amount: {}", amount.setScale(2, RoundingMode.HALF_UP));
        log.debug("using USD instead of TND, PayPal doesn't support TND");

        return new PayPalOrderRequest(
                "AUTHORIZE",
                List.of(new PayPalOrderRequest.PayPalPurchaseUnit(
                        new PayPalOrderRequest
                                .PayPalPurchaseUnit
                                .PayPalAmount("USD", amount.setScale(2, RoundingMode.HALF_UP)),
                        "Order Payment testing"
                )),
                new PayPalOrderRequest.PayPalApplicationContext(
                        props.returnUrl()+"?orderId="+orderId ,
                        props.cancelUrl())
        );
    }

    private CreatePaypalOrderResponse extractOrderResponse(PayPalOrderResponse response) {
        return response.links().stream()
                .filter(link -> "approve".equals(link.rel()) || "payer-action".equals(link.rel()))
                .findFirst()
                .map(link -> new CreatePaypalOrderResponse(response.id(), link.href()))
                .orElseThrow(() -> new PayPalApiException("Approval URL not found in PayPal response"));
    }

    private AuthorizePaypalOrderResponse extractAuthorizationResponse(PayPalAuthorizeResponse response) {
        return response.purchaseUnits().stream()
                .findFirst()
                .map((PayPalAuthorizeResponse
                        .PayPalPurchaseUnitAuthorized::payments))
                .map(PayPalAuthorizeResponse.PayPalPurchaseUnitAuthorized
                        .PayPalPayments::authorizations)
                .flatMap(authorizations -> authorizations.stream().findFirst())
                .map(authorization -> new AuthorizePaypalOrderResponse(response.id(), authorization.id()))
                .orElseThrow(() -> new PayPalApiException("Authorization ID not found in PayPal response"));
    }


}