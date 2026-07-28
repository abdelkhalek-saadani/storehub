package com.abdelkhalek.storehub.order.order;

import com.abdelkhalek.storehub.order.order.event.PaymentStatusUpdateEvent;
import com.abdelkhalek.storehub.order.order.service.OrderStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.AcknowledgableDelivery;
import reactor.rabbitmq.ConsumeOptions;
import reactor.rabbitmq.Receiver;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final Receiver receiver;
    private final ObjectMapper objectMapper;
    private final OrderStatusService orderStatusService;

    @PostConstruct
    public void startListening() {
        receiver.consumeManualAck("payment.status.queue", new ConsumeOptions())
                .flatMap(this::handleDelivery)
                .subscribe(
                        v -> {},
                        err -> log.error("Listener terminated unexpectedly!", err)
                );;
    }

    private Mono<Void> handleDelivery(AcknowledgableDelivery delivery) {
        return Mono.fromCallable(() -> objectMapper.readValue(delivery.getBody(), PaymentStatusUpdateEvent.class))
                .doOnNext(event -> log.debug("Received OrderStatusUpdate event: {}", event))
                .flatMap(event -> orderStatusService.handleStatusUpdate(event.orderId(),event.newStatus()))
                .doOnSuccess(v -> delivery.ack())
                .doOnError(e -> {
                    log.error("Failed to process OrderStatusUpdate event", e);
                    delivery.nack(false);
                })
                .onErrorResume(e -> Mono.empty());
    }
}
