package com.abdelkhalek.storehub.order.order.spi;

import com.abdelkhalek.storehub.order.order.models.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderRepository {

    Mono<Order> save(Order order);
    Mono<Order> findById(UUID orderId);
    Mono<Order> findByIdempotencyKey(UUID idempotencyKey);
    Mono<Order> findByPaymentOrderId(String paymentOrderId);

    Flux<Order> findAll();
    Mono<Void> deleteAll();

    Mono<Long> count();

    Mono<Order> findByIdAndEmail(UUID orderId, String email);
}
