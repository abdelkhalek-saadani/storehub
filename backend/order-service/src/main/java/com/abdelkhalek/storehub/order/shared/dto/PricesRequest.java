package com.abdelkhalek.storehub.order.shared.dto;

import java.util.List;
import java.util.UUID;

public record PricesRequest(
        UUID storeId,
        List<PriceItemRequest> items
) {
    public static PricesRequest empty() {
        return new PricesRequest(UUID.fromString("00000000-0000-0000-0000-000000000000"), List.of());
    }
}
