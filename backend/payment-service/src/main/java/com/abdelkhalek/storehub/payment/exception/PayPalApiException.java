package com.abdelkhalek.storehub.payment.exception;

public class PayPalApiException extends RuntimeException {
    public PayPalApiException(String message) {
        super(message);
    }

}