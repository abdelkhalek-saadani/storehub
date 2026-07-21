package com.abdelkhalek.storehub.order.order.spi;

import com.abdelkhalek.storehub.order.order.models.DomainEvent;

public interface EventPublisher {

    Void publish(DomainEvent event);

}
