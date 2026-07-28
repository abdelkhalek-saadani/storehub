package com.abdelkhalek.storehub.order.order.spi;

import com.abdelkhalek.storehub.order.order.models.Order;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderRepository {

    Mono<Order> save(Order order);
    Mono<Order> findById(UUID orderId);

}
