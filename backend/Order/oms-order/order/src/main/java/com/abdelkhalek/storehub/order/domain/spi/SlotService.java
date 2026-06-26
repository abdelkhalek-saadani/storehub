package com.abdelkhalek.storehub.order.domain.spi;

import com.abdelkhalek.storehub.order.domain.models.Delivery;
import com.abdelkhalek.storehub.order.domain.models.Slot;
import com.abdelkhalek.storehub.order.domain.models.Store;
import com.abdelkhalek.storehub.order.domain.models.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SlotService {

    Mono<Boolean> checkAvailability(Delivery delivery, Slot slot, Store store);
    Mono<UUID> retain(Delivery delivery,Slot slot, Store store);
    Mono<Void> release(UUID retainId);

}
