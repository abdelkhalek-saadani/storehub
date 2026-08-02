package com.abdelkhalek.storehub.order.order;

import com.abdelkhalek.storehub.order.order.dto.OrderCreatedResponse;
import com.abdelkhalek.storehub.order.order.dto.OrderDto;
import com.abdelkhalek.storehub.order.order.dto.OrderRequest;
import com.abdelkhalek.storehub.order.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    Mono<OrderCreatedResponse> placeOrder(@RequestHeader("Idempotency-Key") UUID idempotencyKey,
                                          @RequestBody OrderRequest orderRequest) {
        log.debug("idem key: {}", idempotencyKey);
        return orderService.placeOrderWithOnlinePayment(idempotencyKey,orderRequest);
    }

    @GetMapping("/{orderId}")
    Mono<OrderDto> getOrder(@PathVariable UUID orderId) {
        return orderService.getOrder(orderId);
    }
}
