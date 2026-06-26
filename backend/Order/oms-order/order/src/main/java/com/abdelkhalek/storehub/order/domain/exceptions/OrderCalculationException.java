package com.abdelkhalek.storehub.order.domain.exceptions;

public class OrderCalculationException extends RuntimeException {
    public OrderCalculationException(String message) {
        super(message);
    }
}
