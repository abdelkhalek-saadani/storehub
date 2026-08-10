package com.abdelkhalek.storehub.catalog.store.event;

import java.time.Instant;
import java.util.UUID;

public record StoreCreatedEvent(UUID storeId,
                                String slug,
                                UUID ownerId,
                                String status,
                                Instant occurredAt) {

}
