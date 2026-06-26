package com.abdelkhalek.storehub.order.infrastructure.implementations;

import com.abdelkhalek.storehub.order.domain.models.DomainEvent;
import com.abdelkhalek.storehub.order.domain.models.OrderCreatedEvent;
import com.abdelkhalek.storehub.order.domain.spi.EventPublisher;
import com.abdelkhalek.storehub.order.infrastructure.models.product.ItemsReleaseEvent;
import com.abdelkhalek.storehub.order.infrastructure.models.slot.SlotReleaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisherImpl implements EventPublisher {
    @Override
    public Void publish(DomainEvent event) {
        log.info("Publishing event {}", event);
        switch (event){
            case ItemsReleaseEvent itemsReleaseEvent :
                System.out.println("ItemsReleaseEvent captured and published");
                break;
            case OrderCreatedEvent orderCreatedEvent:
                System.out.println("OrderCreatedEvent captured and published");
                break;
            case SlotReleaseEvent slotReleaseEvent:
                System.out.println("SlotReleaseEvent captured and published");
                break;
            default:
                throw new IllegalStateException("Unexpected event type: " + event);
        }
        return null;
    }
}
