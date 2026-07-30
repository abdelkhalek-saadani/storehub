package com.abdelkhalek.storehub.order.store.dto;

import java.util.UUID;

public record StoreDto(
        UUID storeId,
        String storeSlug
) {
}
