package com.proxiad.payment.webhook;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class WebhookEvent {
    private String eventType;
    private String resourceType;
    private String resourceId;
    private JsonNode resource;
    private String eventId;
}