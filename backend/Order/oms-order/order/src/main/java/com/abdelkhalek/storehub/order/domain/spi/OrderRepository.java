package com.abdelkhalek.storehub.order.domain.spi;

import com.abdelkhalek.storehub.order.domain.models.Order;
import reactor.core.publisher.Mono;

public interface OrderRepository {

    Mono<Order> save(Order order);

}
