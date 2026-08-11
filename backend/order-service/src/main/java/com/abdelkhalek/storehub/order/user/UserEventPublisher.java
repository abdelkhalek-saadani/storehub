package com.abdelkhalek.storehub.order.user;


import com.abdelkhalek.storehub.order.user.entity.User;
import com.abdelkhalek.storehub.order.user.event.UserCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final Sender sender;
    private final ObjectMapper objectMapper;

    public Mono<Void> userCreated(User user) {
        var event = new UserCreatedEvent(user.getId(), user.getKeycloakId(), Instant.now());
        byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
        log.debug("Publishing event to RabbitMQ: {}, Bytes: {}", event, payload);
        return sender.send(Mono.just(new OutboundMessage("storehub.exchange", "user.created",
                payload)));
    }
}