package com.proxiad.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PayPalWebhookPayload(
        String id,
        @JsonProperty("event_type") String eventType,
        @JsonProperty("resource_type") String resourceType,
        JsonNode resource
) {}
