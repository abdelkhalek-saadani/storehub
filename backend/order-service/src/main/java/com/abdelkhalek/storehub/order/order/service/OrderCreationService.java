package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.cart.entities.CartEntity;
import com.abdelkhalek.storehub.order.cart.service.CartRepository;
import com.abdelkhalek.storehub.order.order.OrderEventPublisher;
import com.abdelkhalek.storehub.order.order.dto.OrderRequest;
import com.abdelkhalek.storehub.order.order.exceptions.OrderCalculationException;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderItem;
import com.abdelkhalek.storehub.order.order.models.OrderStatus;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import com.abdelkhalek.storehub.order.order.spi.PricingService;
import com.abdelkhalek.storehub.order.order.spi.ProductService;
import com.abdelkhalek.storehub.order.order.spi.SlotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderCreationService {

    private final SlotService slotService;
    private final ProductService productService;
    private final PricingService pricingService;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderMapper orderMapper;
    private final ResourceRetentionService retentionService;

    public Mono<Order> findExistingByIdempotencyKey(UUID idempotencyKey) {
        return orderRepository.findByIdempotencyKey(idempotencyKey);
    }

    public Mono<Boolean> checkAvailability(UUID storeId, UUID cartId, UUID slotId) {
        return Mono.zip(
                productService.checkAvailability(storeId, cartId),
                slotService.checkAvailability(storeId, slotId)
        ).map(t -> t.getT1() && t.getT2());
    }

    public Mono<Order> createOrder(UUID userId, OrderRequest orderRequest,
                                   ResourceRetentionService.RetentionResult retention,
                                   UUID idemKey) {
        return cartRepository.findById(orderRequest.cartId())
                .map(cartEntity -> assembleOrder(userId, orderRequest, cartEntity, retention,
                        idemKey))
                .flatMap(this::calculateOrderTotals)
                .flatMap(orderRepository::save)
                .flatMap(this::publishAndReturn)
                .onErrorResume(e -> retentionService.releaseAll(retention).then(Mono.error(e)));
    }

    private Order assembleOrder(UUID userId, OrderRequest orderRequest, CartEntity cartEntity,
                                ResourceRetentionService.RetentionResult retention,
                                UUID idemKey) {
        List<OrderItem> items = orderMapper.fromCartItemEntities(cartEntity.getItems());
        Order order = Order.builder()
                .userId(userId)
                .items(items)
                .inventoryRetainIds(retention.inventoryRetainIds())
                .slotRetainId(retention.slotRetainId())
                .slotId(orderRequest.slotId())
                .storeId(orderRequest.storeId())
                .billingAddress(orderRequest.billingAddress())
                .deliveryAddress(orderRequest.deliveryAddress())
                .status(OrderStatus.CREATED)
                .idempotencyKey(idemKey)
                .build();
        log.debug("Initializing order {}", order);
        return order;
    }

    private Mono<Order> calculateOrderTotals(Order order) {
        return pricingService.calculateOrderTotals(order)
                .onErrorResume(e -> {
                    log.error("Error calculating order totals: {}", e.getClass().getSimpleName());
                    return Mono.error(new OrderCalculationException("Failed to calculate order totals"));
                });
    }

    private Mono<Order> publishAndReturn(Order savedOrder) {
        return orderEventPublisher.orderCreated(savedOrder)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(e -> {
                    log.error("Failed to publish event for order: {}", savedOrder.getId(), e);
                    return Mono.empty();
                })
                .thenReturn(savedOrder);
    }
}
