package com.abdelkhalek.storehub.order.domain.spi;

import com.abdelkhalek.storehub.order.domain.models.DomainEvent;

public interface EventPublisher {

    Void publish(DomainEvent event);

}
