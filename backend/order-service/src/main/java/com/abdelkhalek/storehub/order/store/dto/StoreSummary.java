package com.abdelkhalek.storehub.order.store.dto;

import java.util.UUID;

public record StoreSummary(UUID id,String slug, UUID ownerId, String status) {
}
