package com.proxiad.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.proxiad.payment.dto.AuthorizePaypalOrderResponse;
import com.proxiad.payment.dto.CreatePaypalOrderResponse;
import com.proxiad.payment.exception.PayPalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
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

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // Simple token cache - consider using Redis or proper cache in production
    private final Map<String, TokenInfo> tokenCache = new ConcurrentHashMap<>();

    public PayPalService(ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = objectMapper;
    }

    public CreatePaypalOrderResponse createOrder(String amount, String currency) {
        try {
            String accessToken = getAccessToken();

            Map<String, Object> orderRequest = createOrderPayload(amount, currency);

            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/v2/checkout/orders")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .bodyValue(orderRequest)
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleErrorResponse)
                    .bodyToMono(Map.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
                    .block(Duration.ofSeconds(30));

            log.debug("PayPal order creation response: {}", response);
            return extractOrderResponse(response);

        } catch (Exception e) {
            log.error("Failed to create PayPal order", e);
            throw new PayPalApiException("Failed to create PayPal order", e);
        }
    }

    public String captureAuthorizedPayment(String authorizationId) {
        try {
            String accessToken = getAccessToken();

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

    private String getAccessToken() {
        // Simple token caching - consider using proper cache with TTL
        TokenInfo cachedToken = tokenCache.get("access_token");
        if (cachedToken != null && !cachedToken.isExpired()) {
            return cachedToken.getToken();
        }

        try {
            String auth = Base64.getEncoder()
                    .encodeToString((clientId + ":" + clientSecret).getBytes());

            Map<String, Object> response = webClient.post()
                    .uri(baseUrl + "/v1/oauth2/token")
                    .header("Authorization", "Basic " + auth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .bodyValue("grant_type=client_credentials")
                    .retrieve()
                    .onStatus(status -> status.isError(), this::handleErrorResponse)
                    .bodyToMono(Map.class)
                    .block(Duration.ofSeconds(30));

            String accessToken = response.get("access_token").toString();
            Integer expiresIn = Integer.valueOf(response.get("expires_in").toString());

            // Cache the token with 90% of its lifetime to account for network delays
            long expirationTime = System.currentTimeMillis() + (expiresIn * 900);
            tokenCache.put("access_token", new TokenInfo(accessToken, expirationTime));

            return accessToken;

        } catch (Exception e) {
            log.error("Failed to get access token", e);
            throw new PayPalApiException("Failed to authenticate with PayPal", e);
        }
    }

    private Map<String, Object> createOrderPayload(String amount, String currency) {
        return Map.of(
                "intent", "AUTHORIZE",
                "purchase_units", List.of(
                        Map.of(
                                "amount", Map.of(
                                        "currency_code", currency,
                                        "value", amount
                                )
                        )
                ),
                "application_context", Map.of(
                        "return_url", returnUrl,
                        "cancel_url", cancelUrl
                )
        );
    }

    private CreatePaypalOrderResponse extractOrderResponse(Map<String, Object> response) {
        List<Map<String, String>> links = (List<Map<String, String>>) response.get("links");
        String paymentOrderId = (String) response.get("id");

        return links.stream()
                .filter(link -> "approve".equals(link.get("rel")) || "payer-action".equals(link.get("rel")))
                .findFirst()
                .map(link -> new CreatePaypalOrderResponse(paymentOrderId, link.get("href")))
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

    private Mono<Throwable> handleErrorResponse(org.springframework.web.reactive.function.client.ClientResponse response) {
        return response.bodyToMono(String.class)
                .map(errorBody -> {
                    log.error("PayPal API error - Status: {}, Body: {}", response.statusCode(), errorBody);
                    return new PayPalApiException("PayPal API error: " + response.statusCode() + " - " + errorBody);
                });
    }

    // Simple token info class for caching
    private static class TokenInfo {
        private final String token;
        private final long expirationTime;

        public TokenInfo(String token, long expirationTime) {
            this.token = token;
            this.expirationTime = expirationTime;
        }

        public String getToken() {
            return token;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() >= expirationTime;
        }
    }
}