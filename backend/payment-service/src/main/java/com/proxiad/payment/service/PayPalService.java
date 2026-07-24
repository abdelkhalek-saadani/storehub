package com.proxiad.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxiad.payment.dto.AuthorizePaypalOrderResponse;
import com.proxiad.payment.dto.CreatePaypalOrderResponse;
import com.proxiad.payment.dto.PayPalOrderResponse;
import com.proxiad.payment.exception.PayPalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import reactor.util.retry.Retry;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayPalService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String baseUrl;

    @Value("${paypal.webhook.id}")
    private String webhookId;

    @Value("${paypal.return-url:http://localhost:8080/api/success}")
    private String returnUrl;

    @Value("${paypal.cancel-url:http://localhost:8080/api/cancel}")
    private String cancelUrl;

    private final RestClient restClient;
    private final ObjectMapper objectMapper;


    public CreatePaypalOrderResponse createOrder(BigDecimal amount) {
        Map<String, Object> orderRequest = createOrderPayload(amount);

        PayPalOrderResponse response = restClient.post()
                .uri("/v2/checkout/orders")
                .header("Content-Type", "application/json")
                .body(orderRequest)
                .retrieve()
                .body(PayPalOrderResponse.class);

        log.debug("PayPal order creation response: {}", response);
        if (response == null) {
            throw new PayPalApiException("PayPal returned an empty response body");
        }
        return extractOrderResponse(response);
    }

    public String captureAuthorizedPayment(String authorizationId) {
        try {


            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/v2/payments/authorizations/{authorizationId}/capture", authorizationId)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleErrorResponse)
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .block(Duration.ofSeconds(30));

            log.debug("PayPal capture response: {}", response);
            return response.get("id").toString();

        } catch (Exception e) {
            log.error("Failed to capture authorized payment: {}", authorizationId, e);
            throw new PayPalApiException("Failed to capture authorized payment", e);
        }
    }

    public String voidAuthorizedPayment(String authorizationId) {
        try {
            String accessToken = getAccessToken();

            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/v2/payments/authorizations/{authorizationId}/void", authorizationId)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .header("Prefer", "return=representation")
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleErrorResponse)
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .block(Duration.ofSeconds(30));

            log.debug("PayPal void response: {}", response);
            return response.get("status").toString();

        } catch (Exception e) {
            log.error("Failed to void authorized payment: {}", authorizationId, e);
            throw new PayPalApiException("Failed to void authorized payment", e);
        }
    }

    public String refundCapture(String captureId) {
        try {
            String accessToken = getAccessToken();

            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/v2/payments/captures/{captureId}/refund", captureId)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleErrorResponse)
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .block(Duration.ofSeconds(30));

            log.debug("PayPal refund response: {}", response);
            return response.get("id").toString();

        } catch (Exception e) {
            log.error("Failed to refund capture: {}", captureId, e);
            throw new PayPalApiException("Failed to refund capture", e);
        }
    }

    public AuthorizePaypalOrderResponse authorizeOrder(String orderId) {
        try {
            String accessToken = getAccessToken();

            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/v2/checkout/orders/{orderId}/authorize", orderId)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleErrorResponse)
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .block(Duration.ofSeconds(30));

            log.debug("PayPal authorize response: {}", response);
            return extractAuthorizationResponse(response);

        } catch (Exception e) {
            log.error("Failed to authorize order: {}", orderId, e);
            throw new PayPalApiException("Failed to authorize order", e);
        }
    }

    public boolean verifyWebhook(String payload, String transmissionId, String certUrl,
                                 String authAlgo, String transmissionSig, String transmissionTime) {
        try {
            String accessToken = getAccessToken();
            Map<String, Object> webhookEvent = objectMapper.readValue(payload, Map.class);

            Map<String, Object> verificationRequest = Map.of(
                    "transmission_id", transmissionId,
                    "cert_url", certUrl,
                    "auth_algo", authAlgo,
                    "transmission_sig", transmissionSig,
                    "transmission_time", transmissionTime,
                    "webhook_id", webhookId,
                    "webhook_event", webhookEvent
            );

            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/v1/notifications/verify-webhook-signature")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(verificationRequest)
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleErrorResponse)
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(30));

            boolean isValid = "SUCCESS".equals(response.get("verification_status"));
            log.debug("Webhook verification result: {}", isValid);
            return isValid;

        } catch (Exception e) {
            log.error("Failed to verify webhook", e);
            return false;
        }
    }


    private Map<String, Object> createOrderPayload(BigDecimal amount) {
        log.debug("the amount: {}", amount);
        log.debug("the currency: USD, using USD currency code instead of TND because paypal " +
                "doesnt support TND");
        return Map.of(
                "intent", "AUTHORIZE",
                "purchase_units", List.of(
                        Map.of(
                                "amount", Map.of(
                                        "currency_code", "USD",
                                        "value", amount
                                ),
                                "description", "Order Payment testing"
                        )
                ),
                "application_context", Map.of(
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl
                )
        );
    }

    private CreatePaypalOrderResponse extractOrderResponse(PayPalOrderResponse response) {
        return response.links().stream()
                .filter(link -> "approve".equals(link.rel()) || "payer-action".equals(link.rel()))
                .findFirst()
                .map(link -> new CreatePaypalOrderResponse(response.id(), link.href()))
                .orElseThrow(() -> new PayPalApiException("Approval URL not found in PayPal response"));
    }

    private AuthorizePaypalOrderResponse extractAuthorizationResponse(Map<String, Object> response) {
        String orderId = (String) response.get("id");

        List<Map<String, Object>> purchaseUnits = (List<Map<String, Object>>) response.get("purchase_units");

        return purchaseUnits.stream()
                .findFirst()
                .map(unit -> (Map<String, Object>) unit.get("payments"))
                .map(payments -> (List<Map<String, Object>>) payments.get("authorizations"))
                .flatMap(authorizations -> authorizations.stream().findFirst())
                .map(authorization -> {
                    String authorizationId = (String) authorization.get("id");
                    return new AuthorizePaypalOrderResponse(orderId, authorizationId);
                })
                .orElseThrow(() -> new PayPalApiException("Authorization ID not found in PayPal response"));
    }


}