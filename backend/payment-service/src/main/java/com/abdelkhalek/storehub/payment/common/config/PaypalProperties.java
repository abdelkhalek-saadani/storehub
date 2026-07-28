package com.abdelkhalek.storehub.payment.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "paypal")
public record PaypalProperties(
        String baseUrl,
        Webhook webhook,
        String returnUrl,
        String cancelUrl
) {
    public record Webhook(String id) {}
}
