package com.abdelkhalek.storehub.order.infrastructure.implementations;

import com.abdelkhalek.storehub.order.order.models.DomainEvent;
import com.abdelkhalek.storehub.order.order.models.OrderCreatedEvent;
import com.abdelkhalek.storehub.order.order.spi.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisherImpl implements EventPublisher {
    @Override
    public Void publish(DomainEvent event) {
        log.info("Publishing event {}", event);
        switch (event){
            case OrderCreatedEvent orderCreatedEvent:
                System.out.println("OrderCreatedEvent captured and published");
                break;
            default:
                throw new IllegalStateException("Unexpected event type: " + event);
        }
        return null;
    }
}
