package com.abdelkhalek.storehub.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record PayPalWebhookVerificationRequest(
        @JsonProperty("transmission_id") String transmissionId,
        @JsonProperty("cert_url") String certUrl,
        @JsonProperty("auth_algo") String authAlgo,
        @JsonProperty("transmission_sig") String transmissionSig,
        @JsonProperty("transmission_time") String transmissionTime,
        @JsonProperty("webhook_id") String webhookId,
        @JsonProperty("webhook_event") JsonNode webhookEvent
) {}
