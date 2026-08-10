package com.abdelkhalek.storehub.catalog.inventory.event;

import java.util.List;
import java.util.UUID;

public record ItemsReleaseEvent (
        List<UUID> retainIds
){}
