package com.abdelkhalek.storehub.catalog.slot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReserveSlotRequest(
        @NotNull UUID slotId,
        @NotBlank UUID cartId       // This won't be needed since we will link the reservation to
        // the order later
) {}
