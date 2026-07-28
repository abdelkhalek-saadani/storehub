package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.event.PaymentStatusUpdateEvent;
import com.abdelkhalek.storehub.order.order.models.OrderStatus;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderStatusService {

    private final OrderRepository orderRepository;

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
                .then();
        // send SSE
    }

}
