package com.abdelkhalek.storehub.catalog.store;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class StoreEventListener {

    private final StoreShadowRepository storeShadowRepository;

    @RabbitListener(queues = "store.created.queue")
    public void handleStoreCreated(StoreCreatedEvent event) {
        storeShadowRepository.upsert(event.storeId(), event.slug(), event.ownerId(), event.status(),
                Instant.now());
    }
}