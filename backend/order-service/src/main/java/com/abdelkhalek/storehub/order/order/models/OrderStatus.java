package com.abdelkhalek.storehub.order.order.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    CREATED("Created"),
    AWAITING_PAYMENT("Awaiting Payment"),
    PROCESSING_PAYMENT("Processing your payment"),
    PAYMENT_AUTHORIZED("Paid"),
    PAYMENT_CAPTURED("Paid"),
    PAYMENT_FAILED("Payment failed"),
    PAYMENT_VOIDED("Cancelled"),
    PAYMENT_REFUNDED("Refunded"),
    SHIPPED("Shipped"),
    DELIVERED("Delivered");

    private final String label;

    // to map from OrderStatus to its dto
    public String getCode(){
        return this.name();
    }

}
