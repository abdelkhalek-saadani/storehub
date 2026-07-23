package com.abdelkhalek.storehub.order.order.event;


import java.util.List;
import java.util.UUID;

public record ItemsReleaseEvent (
        List<UUID> retainIds
){}
