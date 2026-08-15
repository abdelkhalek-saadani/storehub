package com.abdelkhalek.storehub.catalog.store;

import com.abdelkhalek.storehub.catalog.store.entity.StoreShadow;
import com.abdelkhalek.storehub.catalog.store.event.StoreCreatedEvent;
import com.abdelkhalek.storehub.catalog.store.repository.StoreShadowRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class StoreEventListenerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StoreShadowRepository storeShadowRepository;

    @Test
    void handleStoreCreated_persistsStoreShadow_whenEventReceived() {
        UUID storeId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        StoreCreatedEvent event = new StoreCreatedEvent(storeId, "my-slug", ownerId, "ACTIVE", Instant.now());

        rabbitTemplate.convertAndSend("store.created.queue", event);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<StoreShadow> shadow = storeShadowRepository.findById(storeId);
            assertThat(shadow).isPresent();
            assertThat(shadow.get().getSlug()).isEqualTo("my-slug");
            assertThat(shadow.get().getOwnerId()).isEqualTo(ownerId);
            assertThat(shadow.get().getStatus()).isEqualTo("ACTIVE");
        });
    }

    @Test
    void handleStoreCreated_updatesExistingRow_onDuplicateEvent() {
        UUID storeId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        rabbitTemplate.convertAndSend("store.created.queue",
                new StoreCreatedEvent(storeId, "old-slug", ownerId, "ACTIVE", Instant.now()));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(storeShadowRepository.findById(storeId)).isPresent());

        // same id, updated fields -> should upsert, not duplicate
        rabbitTemplate.convertAndSend("store.created.queue",
                new StoreCreatedEvent(storeId, "new-slug", ownerId, "ACTIVE", Instant.now()));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<StoreShadow> shadow = storeShadowRepository.findById(storeId);
            assertThat(shadow).isPresent();
            assertThat(shadow.get().getSlug()).isEqualTo("new-slug");
        });

        assertThat(storeShadowRepository.count()).isEqualTo(1); // no duplicate row
    }
}
