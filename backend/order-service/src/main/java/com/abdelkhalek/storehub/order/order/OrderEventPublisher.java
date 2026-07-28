package com.abdelkhalek.storehub.order.order;


import com.abdelkhalek.storehub.order.common.config.StorehubProperties;
import com.abdelkhalek.storehub.order.order.event.ItemsReleaseEvent;
import com.abdelkhalek.storehub.order.order.event.OrderCreateEvent;
import com.abdelkhalek.storehub.order.order.event.SlotReleaseEvent;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(StorehubProperties.class)
public class OrderEventPublisher {

    private final StorehubProperties props;

    private final Sender sender;
    private final ObjectMapper objectMapper;

    public Mono<Void> itemsReleased(List<UUID> retainIds) {
        var event = new ItemsReleaseEvent(retainIds);
        byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }

        log.debug("Publishing items release event to RabbitMQ: {}, Bytes: {}", event, payload);
        return sender.send(Mono.just(new OutboundMessage(props.rabbit().exchange(), "items.released",
                payload)));
    }

    public Mono<Void> slotReleased(UUID retainId) {
        var event = new SlotReleaseEvent(retainId);
        byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
        log.debug("Publishing slot release event to RabbitMQ: {}, Bytes: {}", event, payload);
        return sender.send(Mono.just(new OutboundMessage(props.rabbit().exchange(), "slot.released",
                payload)));
    }

    public Mono<Void> orderCreated(Order order) {
        var event = new OrderCreateEvent(order.getId(), order.getSlotRetainId(), order.getInventoryRetainIds());
        byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(event);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }
        log.debug("Publishing order create event to RabbitMQ: {}, Bytes: {}", event, payload);
        return sender.send(Mono.just(new OutboundMessage(props.rabbit().exchange(), "order.created",
                payload)));
    }
}
