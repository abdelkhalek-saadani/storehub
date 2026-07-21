package com.abdelkhalek.storehub.order.order.spi;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SlotService {

    Mono<Boolean> checkAvailability(UUID storeId, UUID slotId);
    Mono<UUID> retain(UUID storeId, UUID slotId);
    Mono<Void> release(UUID retainId);

}
