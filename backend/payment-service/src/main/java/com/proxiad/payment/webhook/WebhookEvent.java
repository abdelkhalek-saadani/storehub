package com.proxiad.payment.webhook;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class WebhookEvent {
    private String eventType;
    private String resourceType;
    private String resourceId;
    private Map<String, Object> resource;
    private String eventId;
}