package com.abdelkhalek.storehub.catalog.inventory.dto;

import java.util.List;
import java.util.UUID;

public record ReservationResponse(
        List<UUID> reservationIds
) {
}
