package com.abdelkhalek.storehub.payment.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum ResourceType {
    AUTHORIZATION("authorization"),
    CHECKOUT_ORDER("checkout-order"),
    CAPTURE("capture"),
    ID("id"),
    REFUND("refund");

    private final String value;

    private static final Map<String, ResourceType> VALUE_MAP =
            Arrays.stream(values())
                    .collect(Collectors.toMap(ResourceType::getValue, Function.identity()));

    ResourceType(String value) {
        this.value = value;
    }

    public static ResourceType fromValue(String value) {
        ResourceType type = VALUE_MAP.get(value);
        if (type == null) {
            throw new IllegalArgumentException("Unknown resource type: " + value);
        }
        return type;
    }
}
