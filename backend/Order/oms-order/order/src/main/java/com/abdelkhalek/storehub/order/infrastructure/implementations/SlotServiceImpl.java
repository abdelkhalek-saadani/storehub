package com.abdelkhalek.storehub.order.infrastructure.implementations;

import com.abdelkhalek.storehub.order.domain.models.Delivery;
import com.abdelkhalek.storehub.order.domain.models.Slot;
import com.abdelkhalek.storehub.order.domain.models.Store;
import com.abdelkhalek.storehub.order.domain.spi.SlotService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.UUID;

//@Service
@Slf4j
public class SlotServiceImpl implements SlotService {
    @Override
    public Mono<Boolean> checkAvailability(Delivery delivery, Slot slot, Store store) {
        log.info("Checking slot availability..." +
                "delivery: {}, slot: {}, store: {}", delivery, slot, store);
        return Mono.just(true);
    }

    @Override
    public Mono<UUID> retain(Delivery delivery, Slot slot, Store store) {
        log.info("Retaining this slot {} in this store {} with delivery {}...", slot, store, delivery);
        return Mono.fromSupplier(UUID::randomUUID);
    }

    @Override
    public Mono<Void> release(UUID retainId) {
        log.info("Releasing the slot using the retain id {}...", retainId);
        return Mono.empty();
    }
}
