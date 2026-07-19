package com.abdelkhalek.storehub.catalog.user;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserShadowRepository userShadowRepository;

    @RabbitListener(queues = "user.created.queue")
    public void handleUserCreated(UserCreatedEvent event) {
        userShadowRepository.save(new UserShadow(event.userId(), event.keycloakId(),Instant.now()));
    }
}
