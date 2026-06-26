package com.abdelkhalek.storehub.order.domain.exceptions;

public class UnavailableDeliveryException extends RuntimeException {
    public UnavailableDeliveryException(String message) {
        super(message);
    }
}
