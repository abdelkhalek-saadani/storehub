package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.event.PaymentStatusUpdateEvent;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderStatus;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderStatusService {

    private final OrderRepository orderRepository;

    Map<UUID, Sinks.Many<OrderStatus>> sinks = new ConcurrentHashMap<>();

    public Flux<OrderStatus> orderStatusStream(UUID orderId) {
        Sinks.Many<OrderStatus> sink = sinks.computeIfAbsent(orderId,
                id -> Sinks.many().multicast().onBackpressureBuffer());
        return sink.asFlux().doFinally(signal -> sinks.remove(orderId));
    }

    public Mono<Void> handleStatusUpdate(UUID orderId,
                                         PaymentStatusUpdateEvent.PaymentStatus newStatus) {
        // lookup the order
        return orderRepository.findById(orderId)
                // set its status
                .flatMap((order ->
                {
                    switch (newStatus) {
                        case CREATED -> order.setStatus(OrderStatus.AWAITING_PAYMENT);
                        case CAPTURED -> order.setStatus(OrderStatus.PAYMENT_CAPTURED);
                        case CANCELLED -> order.setStatus(OrderStatus.PAYMENT_VOIDED);
                        case REFUNDED -> order.setStatus(OrderStatus.PAYMENT_REFUNDED);
                        case APPROVED -> order.setStatus(OrderStatus.PROCESSING_PAYMENT);
                        case AUTHORIZED -> order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
                    }
                    // save
                    return orderRepository.save(order);
                }))
                // send SSE
                .doOnNext(this::sendSSE)
                .then();

    }

    public void sendSSE(Order order) {
        Sinks.Many<OrderStatus> sink = sinks.get(order.getId());
        if (sink != null) {
            Sinks.EmitResult result = sink.tryEmitNext(order.getStatus());
            if (result.isFailure()) {
                log.warn("Failed to emit order status update: {} for order {} and status: {}",
                        result, order.getId(), order.getStatus());
            }
        }
    }

    public Mono<Order> updateStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        return orderRepository.save(order).doOnNext(this::sendSSE);
    }

    public Mono<OrderStatus> getById(UUID orderId) {
        return orderRepository.findById(orderId).map(Order::getStatus);
    }

}
