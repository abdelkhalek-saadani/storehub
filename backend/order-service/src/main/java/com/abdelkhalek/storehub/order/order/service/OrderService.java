package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.cart.service.CartService;
import com.abdelkhalek.storehub.order.cart.service.OwnerResolver;
import com.abdelkhalek.storehub.order.order.dto.*;
import com.abdelkhalek.storehub.order.order.exceptions.OrderNotFoundException;
import com.abdelkhalek.storehub.order.order.exceptions.UnauthorizedAccessException;
import com.abdelkhalek.storehub.order.order.exceptions.UnavailableException;
import com.abdelkhalek.storehub.order.order.mapper.OrderMapper;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderStatus;
import com.abdelkhalek.storehub.order.shared.model.ServiceResult;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import com.abdelkhalek.storehub.order.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
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
    private final OrderStatusService orderStatusService;
    private final OwnerResolver ownerResolver;
    private final CartService cartService;

    public Mono<Order> placeOrder(OrderRequest orderRequest, UUID idempotencyKey, UUID guestId) {
        return orderCreationService.checkAvailability(orderRequest.storeId(), orderRequest.cartId(), orderRequest.slotId())
                .flatMap(isAvailable -> {
                    if (!isAvailable) {
                        log.debug("Unavailable items in cart {} or slot {}", orderRequest.cartId(),
                                orderRequest.slotId());
                        return Mono.error(new UnavailableException("Unavailable items or slot"));
                    }
                    return ownerResolver.resolveOwner(guestId)
                            .flatMap(owner -> retentionService.retainAll(
                                            orderRequest.storeId(), orderRequest.cartId(), orderRequest.slotId())
                                    .flatMap(retention -> orderCreationService.createOrder(owner,
                                            orderRequest, retention, idempotencyKey)));
                });
    }

    public Mono<ServiceResult<OrderCreatedResponse>> placeOrderWithOnlinePayment(UUID idempotencyKey,
                                                                                 OrderRequest orderRequest,
                                                                                 UUID guestId) {
        return orderCreationService
                .findExistingByIdempotencyKey(idempotencyKey)
                .map(OrderCreatedResponse::from)
                .map((ocr -> guestId != null ?
                        ServiceResult.forGuest(ocr, guestId)
                        : ServiceResult.forUser(ocr)))
                .switchIfEmpty(
                        placeOrder(orderRequest, idempotencyKey, guestId)
                                .flatMap(orderPaymentService::attachPaymentAndSave)
                                .flatMap((order) ->
                                        cartService.clearCart(orderRequest.cartId())
                                                .timeout(Duration.ofSeconds(5))
                                                .onErrorResume(e -> {
                                                    log.error("Failed to clear cart {} after placing order {}",
                                                            orderRequest.cartId(),
                                                            order.getId(), e);
                                                    return Mono.empty();
                                                })
                                                .thenReturn(order))
                                .map(order -> {
                                    OrderCreatedResponse ocr = OrderCreatedResponse.from(order);
                                    return order.getGuestId() != null ?
                                            ServiceResult.forGuest(ocr, guestId) :
                                            ServiceResult.forUser(ocr);
                                })
                );
    }

    public Mono<OrderDto> getOrder(UUID orderId) {
        return orderRepository.findById(orderId).map(orderMapper::toOrderDto);
    }

    public Mono<OrderDto> getOrderByToken(String paymentOrderId) {
        log.debug("paymentOrderId from service: {}", paymentOrderId);
        return orderRepository.findByPaymentOrderId(paymentOrderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Order with payment order " +
                        "id " + paymentOrderId + "is not found")))
                .flatMap(order -> userService.getCurrentUser()
                        .flatMap(user -> {
                            log.debug("user: {}", user);
                            log.debug("order: {}", order);
                            if (user.getId().equals(order.getUserId())) {
                                log.debug("returning order: {}", order);
                                return Mono.just(orderMapper.toOrderDto(order));
                            }
                            return Mono.error(new UnauthorizedAccessException(
                                    "User " + user.getId() + " is not authorized to see order " + order.getId()
                                            + " with user id " + order.getUserId()));
                        }));
    }

    private Mono<Order> getOrderForUser(UUID orderId) {
        return orderRepository.findById(orderId)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Order with id " +
                        orderId + "is not found")))
                .flatMap(order -> userService.getCurrentUser()
                        .flatMap(user -> {
                            log.debug("current user: {}", user);
                            log.debug("the found order: {}", order);
                            if (user.getId().equals(order.getUserId())) {
                                log.debug("order user id matches current user, returning order: {}",
                                        order);
                                return Mono.just(order);
                            }
                            return Mono.error(new UnauthorizedAccessException(
                                    "User " + user.getId() + " is not authorized to see order " + order.getId()
                                            + " with user id " + order.getUserId()));
                        }));
    }

    public Mono<OrderCancelResponse> cancelOrder(UUID orderId) {
        return getOrderForUser(orderId)
                .flatMap(this::cancelOrder);
    }

    public Mono<OrderCancelResponse> cancelOrder(UUID orderId, String email) {
        return getOrderByIdAndEmail(orderId, email)
                .flatMap(this::cancelOrder);

    }

    private Mono<OrderCancelResponse> cancelOrder(Order order) {
        if (order.getStatus().equals(OrderStatus.VOID_REQUESTED))
            return Mono.just(new OrderCancelResponse(order.getId(),
                    order.getPaymentId(),
                    orderMapper.toDto(OrderStatus.VOID_REQUESTED),
                    "A void request already submitted"));
        Mono<PaymentResponse> prMono =
                orderPaymentService.voidAuthorizedPayment(order.getPaymentId());
        return prMono.flatMap((pr) ->
                orderStatusService.updateStatus(order, OrderStatus.VOID_REQUESTED)
                        .map((order1 -> new OrderCancelResponse(order1.getId(),
                                order1.getPaymentId(),
                                orderMapper.toDto(order1.getStatus()), pr.message()))));
    }

    public Mono<Order> getOrderByIdAndEmail(UUID orderId, String email) {
        return orderRepository.findByIdAndEmail(orderId, email)
                .switchIfEmpty(Mono.error(new OrderNotFoundException("Order with id " +
                        orderId + "is not found")))
                ;
    }
}
