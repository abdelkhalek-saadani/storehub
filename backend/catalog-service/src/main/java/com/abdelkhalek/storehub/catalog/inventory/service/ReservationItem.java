package com.abdelkhalek.storehub.catalog.inventory.service;

import java.util.UUID;

public record ReservationItem(UUID productId, int quantity) {
}
