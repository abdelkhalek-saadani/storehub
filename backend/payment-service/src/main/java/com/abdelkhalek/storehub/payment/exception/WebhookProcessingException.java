package com.abdelkhalek.storehub.payment.exception;

public class WebhookProcessingException extends RuntimeException {
    public WebhookProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}