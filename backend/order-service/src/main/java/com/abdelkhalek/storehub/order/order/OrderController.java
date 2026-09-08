package com.abdelkhalek.storehub.order.order;

import com.abdelkhalek.storehub.order.order.dto.*;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.service.OrderService;
import com.abdelkhalek.storehub.order.order.service.OrderStatusService;
import com.abdelkhalek.storehub.order.shared.model.ServiceResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Orders", description = "Order placement, tracking, and cancellation")
public class OrderController {

    private final OrderService orderService;
    private final OrderStatusService orderStatusService;
    private final OrderMapper orderMapper;

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Place a new order with online payment",
            description = "Requires an idempotency key to prevent duplicate order creation.")
    @PostMapping
    Mono<OrderCreatedResponse> placeOrder(@RequestHeader("Idempotency-Key") UUID idempotencyKey,
                                          @RequestHeader(value = "X-Guest-Id", required = false) UUID guestId,
                                          @RequestBody OrderRequest orderRequest,
                                          ServerWebExchange exchange) {
        log.debug("idem key: {}", idempotencyKey);
        return orderService
                .placeOrderWithOnlinePayment(idempotencyKey, orderRequest, guestId)
                .doOnNext(result -> {
                    if (result.isGuest()) exchange.getResponse().getHeaders().set("X-Guest-Id",
                            result.guestId().toString());
                })
                .map(ServiceResult::body);
    }

    @Operation(summary = "Stream real-time order status updates",
            description = "Server-Sent Events stream; emits the current status followed by live updates.")
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

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Cancel an order",
            description = "Guests provide their email to authorize cancellation; authenticated users are resolved from context.")
    @PostMapping("/{orderId}/void")
    Mono<OrderCancelResponse> cancelOrder(
            @PathVariable UUID orderId,
            @RequestHeader(value = "From", required = false) String email
    ) {
        // This path for guests, a guest can cancel an order by its id and email
        if (email != null) {
            log.debug("Cancelling order for guest: {}, orderId: {}", email, orderId);
            return orderService.cancelOrder(orderId, email);
        }
        // For connected users
        log.debug("Cancelling order for connected user, orderId: {}", orderId);
        return orderService.cancelOrder(orderId);
    }

    @Operation(summary = "Get an order by ID")
    @GetMapping("/{orderId}")
    Mono<OrderDto> getOrder(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get an order by payment order ID")
    @GetMapping
    Mono<OrderDto> getOrderByToken(@RequestParam String paymentOrderId) {
        // Get the order with payment order id
        log.debug("paymentOrderId: {}", paymentOrderId);
        return orderService.getOrderByToken(paymentOrderId);
    }

    @Operation(summary = "Get a guest order by order ID and email")
    @PostMapping("/guest")
    Mono<OrderDto> getGuestOrder(@RequestBody TrackOrderRequest trackOrderRequest) {
        //Do the same reactive pipeline as the getOrderByToken, just change the check from userId
        // to email
        log.debug("track order request: {}", trackOrderRequest);
        return orderService.getOrderByIdAndEmail(trackOrderRequest.orderId(), trackOrderRequest.email())
                .map((orderMapper::toOrderDto));
    }
}
