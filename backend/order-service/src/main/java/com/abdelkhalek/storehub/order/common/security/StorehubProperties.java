package com.abdelkhalek.storehub.order.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storehub")
public record StorehubProperties(String internalClientRegistration, String catalogBaseUrl, String kcBaseUrl,
                                 String adminClientRegistration, String realm,
                                 String paymentBaseUrl) {
}