package com.proxiad.payment.dto.paypal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PayPalWebhookVerificationResponse(
        @JsonProperty("verification_status") String verificationStatus
) {}
