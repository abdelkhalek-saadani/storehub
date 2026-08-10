package com.abdelkhalek.storehub.catalog.inventory.dto;

import java.util.UUID;

public record ReservationItem(UUID productId, int quantity) {
}
