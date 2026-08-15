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
        UserShadow shadow = userShadowRepository.findById(event.userId())
                .orElse(new UserShadow());
        shadow.setId(event.userId());
        shadow.setKeycloakId(event.keycloakId());
        shadow.setSyncedAt(Instant.now());
        userShadowRepository.save(shadow);    }
}
