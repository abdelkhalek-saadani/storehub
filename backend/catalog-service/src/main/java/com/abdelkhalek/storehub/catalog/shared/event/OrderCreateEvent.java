package com.abdelkhalek.storehub.catalog.shared.event;

import java.util.List;
import java.util.UUID;

public record OrderCreateEvent (
        UUID orderId,
        UUID slotRetainId,
        List<UUID> inventoryRetainIds
) {}
