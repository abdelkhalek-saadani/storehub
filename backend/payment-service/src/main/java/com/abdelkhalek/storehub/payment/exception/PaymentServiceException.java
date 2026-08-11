package com.abdelkhalek.storehub.payment.exception;

public class PaymentServiceException extends RuntimeException {


    public PaymentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}