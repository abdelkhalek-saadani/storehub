package com.abdelkhalek.storehub.order.order;

import com.abdelkhalek.storehub.order.common.config.StorehubProperties;
import com.abdelkhalek.storehub.order.order.event.PaymentStatusUpdateEvent;
import com.abdelkhalek.storehub.order.order.service.OrderStatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private static final String QUEUE = "payment.status.queue";
    private static final String ROUTING_KEY = "payment.status.updated";

    private final Receiver receiver;
    private final Sender sender;
    private final ObjectMapper objectMapper;
    private final OrderStatusService orderStatusService;
    private final StorehubProperties props;

    @PostConstruct
    public void startListening() {
        String exchange = props.rabbit().exchange();
        String dlx = exchange + ".dlx";
        String dlq = QUEUE.replace(".queue", ".dlq");

        sender.declareExchange(ExchangeSpecification.exchange(exchange).type("topic").durable(true))
                .then(sender.declareExchange(ExchangeSpecification.exchange(dlx).type("topic").durable(true)))
                .then(sender.declareQueue(QueueSpecification.queue(QUEUE)
                        .durable(true)
                        .arguments(Map.of(
                                "x-dead-letter-exchange", dlx,
                                "x-dead-letter-routing-key", ROUTING_KEY
                        ))))
                .then(sender.bind(BindingSpecification.binding(exchange, ROUTING_KEY, QUEUE)))
                .then(sender.declareQueue(QueueSpecification.queue(dlq).durable(true)))
                .then(sender.bind(BindingSpecification.binding(dlx, ROUTING_KEY, dlq)))
                .thenMany(receiver.consumeManualAck(QUEUE, new ConsumeOptions()))
                .flatMap(this::handleDelivery)
                .subscribe(
                        v -> {},
                        err -> log.error("Listener terminated unexpectedly!", err)
                );
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
