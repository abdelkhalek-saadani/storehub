package com.abdelkhalek.storehub.order.store.model;

import java.time.Instant;
import java.util.UUID;

public record StoreCreatedEvent(UUID storeId, String slug, UUID ownerId, String status,
                                Instant occurredAt) {
}
