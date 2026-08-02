package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.dto.OrderCreatedResponse;
import com.abdelkhalek.storehub.order.order.dto.OrderDto;
import com.abdelkhalek.storehub.order.order.dto.OrderRequest;
import com.abdelkhalek.storehub.order.order.exceptions.UnavailableException;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import com.abdelkhalek.storehub.order.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderService {

    private final UserService userService;
    private final ResourceRetentionService retentionService;
    private final OrderCreationService orderCreationService;
    private final OrderPaymentService orderPaymentService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public Mono<Order> placeOrder(OrderRequest orderRequest, UUID idempotencyKey) {
        return orderCreationService.checkAvailability(orderRequest.storeId(), orderRequest.cartId(), orderRequest.slotId())
                .flatMap(isAvailable -> {
                    if (!isAvailable) {
                        return Mono.error(new UnavailableException("Unavailable items or slot"));
                    }
                    return userService.getCurrentUser()
                            .flatMap(user -> retentionService.retainAll(
                                            orderRequest.storeId(), orderRequest.cartId(), orderRequest.slotId())
                                    .flatMap(retention -> orderCreationService.createOrder(user.getId(), orderRequest, retention, idempotencyKey)));
                });
    }

    public Mono<OrderCreatedResponse> placeOrderWithOnlinePayment(UUID idempotencyKey,
                                                                  OrderRequest orderRequest) {
        return orderCreationService
                .findExistingByIdempotencyKey(idempotencyKey)
                .map(OrderCreatedResponse::from)
                .switchIfEmpty(
                        placeOrder(orderRequest, idempotencyKey).flatMap(orderPaymentService::attachPaymentAndSave));
    }

    public Mono<OrderDto> getOrder(UUID orderId) {
        return orderRepository.findById(orderId).map(orderMapper::toOrderDto);
    }
}
