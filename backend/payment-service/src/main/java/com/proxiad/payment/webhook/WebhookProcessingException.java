package com.proxiad.payment.webhook;

public class WebhookProcessingException extends RuntimeException {
    public WebhookProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}