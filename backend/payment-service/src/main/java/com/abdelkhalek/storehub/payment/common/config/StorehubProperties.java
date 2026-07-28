package com.proxiad.payment.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storehub")
public record StorehubProperties
        (Rabbit rabbit){
    public record Rabbit(
            String exchange
    ){}
}
