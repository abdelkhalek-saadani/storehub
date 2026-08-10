package com.abdelkhalek.storehub.order.store.dto;

import com.abdelkhalek.storehub.order.store.model.Store;

import java.util.UUID;

public record StoreDto(
        UUID storeId,
        String storeSlug,
        String storeName
) {
    public static StoreDto from(Store store) {
        return new StoreDto(store.getId(), store.getSlug(), store.getName());
    }
}
