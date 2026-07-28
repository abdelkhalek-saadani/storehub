package com.abdelkhalek.storehub.payment.exception;

public class WebhookVerificationException extends RuntimeException {
    public WebhookVerificationException(String message) {
        super(message);
    }
}