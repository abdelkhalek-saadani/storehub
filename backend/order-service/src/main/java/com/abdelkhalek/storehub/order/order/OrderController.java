package com.abdelkhalek.storehub.order.order;

import com.abdelkhalek.storehub.order.order.dto.*;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.models.ServiceResult;
import com.abdelkhalek.storehub.order.order.service.OrderService;
import com.abdelkhalek.storehub.order.order.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusService orderStatusService;
    private final OrderMapper orderMapper;


    @PostMapping
    Mono<OrderCreatedResponse> placeOrder(@RequestHeader("Idempotency-Key") UUID idempotencyKey,
                                          @RequestBody OrderRequest orderRequest,
                                          ServerWebExchange exchange) {
        log.debug("idem key: {}", idempotencyKey);
        String guestId = exchange.getRequest().getHeaders().getFirst("X-Guest-Id");
        return orderService
                .placeOrderWithOnlinePayment(idempotencyKey, orderRequest, guestId)
                .doOnNext(result -> {
                    if (result.isGuest()) exchange.getResponse().getHeaders().set("X-Guest-Id",
                            result.guestId().toString());
                })
                .map(ServiceResult::body);
    }

    @GetMapping(value = "/{orderId}/track", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<OrderStatusDto>> track(@PathVariable UUID orderId) {
        log.debug("orderId: {}", orderId);
        log.debug("tracking orderId: {}", orderId);
        Mono<ServerSentEvent<OrderStatusDto>> current = orderStatusService.getById(orderId)
                .map(status -> ServerSentEvent.builder(orderMapper.toDto(status)).build());
        Flux<ServerSentEvent<OrderStatusDto>> live = orderStatusService.orderStatusStream(orderId)
                .map(status -> ServerSentEvent.builder(orderMapper.toDto(status)).build());
        return Flux.concat(current, live);
    }

    @PostMapping("/{orderId}/void")
    Mono<OrderCancelResponse> cancelOrder(
            @PathVariable UUID orderId
    ) {
        return orderService.cancelOrder(orderId);
    }

    @GetMapping("/{orderId}")
    Mono<OrderDto> getOrder(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping
    Mono<OrderDto> getOrderByToken(@RequestParam String paymentOrderId) {
        // Get the order with payment order id
        log.debug("paymentOrderId: {}", paymentOrderId);
        return orderService.getOrderByToken(paymentOrderId);
    }

    @PostMapping("/guest")
    Mono<OrderDto> getGuestOrder(@RequestBody TrackOrderRequest trackOrderRequest) {
        //Do the same reactive pipeline as the getOrderByToken, just change the check from userId
        // to email
        log.debug("track order request: {}", trackOrderRequest);
        return orderService.getOrderByIdAndEmail(trackOrderRequest.orderId(), trackOrderRequest.email());
    }
}
