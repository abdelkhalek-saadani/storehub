package com.abdelkhalek.storehub.order.store;

import com.abdelkhalek.storehub.order.common.config.StorehubProperties;
import com.abdelkhalek.storehub.order.store.model.Store;
import com.abdelkhalek.storehub.order.store.model.StoreCreatedEvent;
import com.abdelkhalek.storehub.order.user.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(StorehubProperties.class)
public class StoreEventPublisher {

    private final StorehubProperties props;
    private final Sender sender;
    private final ObjectMapper objectMapper;

    public Mono<Void> storeCreated(User user, Store store) {
        var event = new StoreCreatedEvent(store.getId(),store.getSlug(), user.getId(),
                "ACTIVE", Instant.now());
        byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
        log.debug("Publishing event to RabbitMQ: {}, Bytes: {}", event, payload);
        return sender.send(Mono.just(new OutboundMessage(props.rabbit().exchange(), "store.created",
                payload)));
    }
}