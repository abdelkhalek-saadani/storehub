package com.abdelkhalek.storehub.order.user.event;

import java.time.Instant;
import java.util.UUID;

public record UserCreatedEvent (
        UUID userId,
        String keycloakId,
        Instant occurredAt
){}
