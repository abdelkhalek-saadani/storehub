package com.abdelkhalek.storehub.catalog.user;

import java.time.Instant;
import java.util.UUID;

public record UserCreatedEvent (
        UUID userId,
        String keycloakId,
        Instant occurredAt
){}

