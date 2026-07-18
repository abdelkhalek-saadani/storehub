package com.abdelkhalek.storehub.order.store.model;

import java.util.UUID;

public record StoreSummary(UUID id, UUID ownerId, String status) {
}
