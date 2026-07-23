package com.abdelkhalek.storehub.order.order.event;

import java.util.List;
import java.util.UUID;

public record OrderCreateEvent (
        UUID orderId,
        UUID slotRetainId,
        List<UUID> inventoryRetainIds
) {}
