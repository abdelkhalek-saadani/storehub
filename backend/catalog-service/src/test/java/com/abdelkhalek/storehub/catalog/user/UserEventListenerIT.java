package com.abdelkhalek.storehub.catalog.user;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
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
@ActiveProfiles("test")
class UserEventListenerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserShadowRepository userShadowRepository;

    @Test
    void handleUserCreated_persistsUserShadow_whenEventReceived() {
        UUID userId = UUID.randomUUID();
        String keycloakId = "kc-" + UUID.randomUUID();
        UserCreatedEvent event = new UserCreatedEvent(userId, keycloakId, Instant.now());

        rabbitTemplate.convertAndSend("user.created.queue", event);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<UserShadow> shadow = userShadowRepository.findById(userId);
            assertThat(shadow).isPresent();
            assertThat(shadow.get().getKeycloakId()).isEqualTo(keycloakId);
        });
    }

    @Test
    void handleUserCreated_updatesExistingRow_onDuplicateEvent() {
        UUID userId = UUID.randomUUID();
        String keycloakId = "kc-" + UUID.randomUUID();

        rabbitTemplate.convertAndSend("user.created.queue",
                new UserCreatedEvent(userId, keycloakId, Instant.now()));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
                assertThat(userShadowRepository.findById(userId)).isPresent());

        rabbitTemplate.convertAndSend("user.created.queue",
                new UserCreatedEvent(userId, "different-keycloak-id", Instant.now()));

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Optional<UserShadow> shadow = userShadowRepository.findById(userId);
            assertThat(shadow).isPresent();
            assertThat(shadow.get().getKeycloakId()).isEqualTo("different-keycloak-id");
        });

        assertThat(userShadowRepository.count()).isEqualTo(1);
    }
}
