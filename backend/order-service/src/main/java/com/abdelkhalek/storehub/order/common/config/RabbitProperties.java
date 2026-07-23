package com.abdelkhalek.storehub.order.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storehub.rabbit")
public record RabbitProperties(String exchange) {}
