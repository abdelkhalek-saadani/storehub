package com.abdelkhalek.storehub.order;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storehub")
public record StorehubProperties(String internalClientRegistration, String catalogBaseUrl) {
}