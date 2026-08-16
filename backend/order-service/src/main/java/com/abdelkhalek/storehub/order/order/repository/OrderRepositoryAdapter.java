package com.abdelkhalek.storehub.order.order.repository;

import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderReactiveRepository orderReactiveRepository;
    private final OrderMapper orderMapper;

    @Override
    public Mono<Order> save(Order order) {
        log.debug("Saving Order: {}", order);
        Mono<OrderEntity> orderEntity =
                orderReactiveRepository.save(orderMapper.toEntity(order))
                        .doOnNext(saved -> log.debug("Saved order entity: {}", saved));
        return orderEntity.map(orderMapper::fromEntity);
    }

    @Override
    public Mono<Order> findById(UUID orderId) {
        log.debug("Getting Order: {}", orderId);
        Mono<OrderEntity> orderEntity =
                orderReactiveRepository.findById(orderId);
        return orderEntity.map(orderMapper::fromEntity);
    }

    @Override
    public Mono<Order> findByIdempotencyKey(UUID idempotencyKey) {
        log.debug("Getting Order with idem key: {}", idempotencyKey);
        return orderReactiveRepository.findByIdempotencyKey(idempotencyKey)
                .map(orderMapper::fromEntity);
    }

    @Override
    public Mono<Order> findByPaymentOrderId(String paymentOrderId) {
        log.debug("Getting Order with id: {}", paymentOrderId);
        return orderReactiveRepository.findByPaymentOrderId(paymentOrderId)
                .map(orderMapper::fromEntity);
    }

    @Override
    public Flux<Order> findAll() {
        return orderReactiveRepository.findAll().map(orderMapper::fromEntity);
    }

    @Override
    public Mono<Void> deleteAll() {
        return orderReactiveRepository.deleteAll();
    }

    @Override
    public Mono<Long> count() {
        return orderReactiveRepository.count();
    }

    @Override
    public Mono<Order> findByIdAndEmail(UUID orderId, String email) {
        log.debug("Getting order with id {} and email {}", orderId, email);
        return orderReactiveRepository.findByIdAndEmail(orderId, email)
                .doOnNext((order -> {
                    log.debug("found this order {}", order);
                }))
                .map(orderMapper::fromEntity);
    }

}
