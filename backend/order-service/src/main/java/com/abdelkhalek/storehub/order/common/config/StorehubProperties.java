package com.abdelkhalek.storehub.order.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storehub")
public record StorehubProperties(String internalClientRegistration, String catalogBaseUrl,
                                 String kcBaseUrl, String adminClientRegistration, String realm,
                                 String paymentBaseUrl, Rabbit rabbit) {
    public record Rabbit(
            String exchange
    ){}
}