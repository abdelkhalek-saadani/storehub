package com.abdelkhalek.storehub.catalog.slot.dto;


import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ReserveSlotRequest(
        @NotNull UUID slotId
) {}
