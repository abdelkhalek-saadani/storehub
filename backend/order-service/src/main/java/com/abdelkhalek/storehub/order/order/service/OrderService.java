package com.abdelkhalek.storehub.order.order.service;

import com.abdelkhalek.storehub.order.order.exceptions.OrderCalculationException;
import com.abdelkhalek.storehub.order.order.exceptions.UnavailableException;
import com.abdelkhalek.storehub.order.order.models.*;
import com.abdelkhalek.storehub.order.order.spi.*;
import com.abdelkhalek.storehub.order.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderService {
    /*
     * Accept order creation arguments
     * Responds with order details or payment link approval
     */
    /* createOrderWithCashPayment(cart, delivery, slot, coupon) returns order details

        // Common Block Start name it common order processing
        if delivery.mode PICKUP
            Checks Store availability CheckStore(delivery(@store),slot)
        if delivery.mode HOME_DELIVERY
            Checks client address can be delivered to or nah CheckCustomerAddress(delivery(@customer), slot)
            Get delivery fee  getDeliveryFee(cart(@items), delivery(@customer))
        retainItems(items,@store or @customer) returns retainId (will use it to release stock if commands annulee or payment fails)
        create an order object (its items contains their final unit prices and their discounts)
        validateCoupon(order,coupon)
        if coupon validated then apply
        // Common Block End

        store order and return Its details, probably will send an event telling others that this command needs to be treated (packing , delivery and so on)

    */
    /* createOrderWithOnlinePayment() returns payment link approval
        // Common Block

        createPaymentApprovalLink(order) this creates an online payment order waiting for approval from customer and returns the link
        returns approval link for the customer

    */

    private final SlotService slotService;
    private final ProductService productService;
    private final PricingService pricingService;
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final PaymentService paymentService;
    private final UserService userService;


    private Mono<Order> placeOrder(OrderRequest orderRequest) {
        // First check if order is valid based on resources availability
        return checkAvailability(orderRequest.storeId(), orderRequest.cartId(), orderRequest.slotId())
                .flatMap(isAvailable -> {
                    if (!isAvailable) {
                        return Mono.error(new UnavailableException("Unavailable items or slot"));
                    }

                    return userService.getCurrentUser()
                            .flatMap(user -> processAvailableOrder(user.getId(), orderRequest));
                });
    }


    /**
     * Processes an order after availability is confirmed
     * Handles inventory, slot retention, total calculation, order creation, and persistence
     */
    private Mono<Order> processAvailableOrder(UUID userId, OrderRequest orderRequest) {
        return executeParallelOperations(orderRequest.storeId(), orderRequest.cartId(), orderRequest.slotId())
                .flatMap(results -> processOperationResults(results, orderRequest, userId));
    }

    private Mono<Tuple2<Result<List<UUID>>, Result<UUID>>> executeParallelOperations(
            UUID storeId, UUID cartId, UUID slotId) {

        Mono<Result<List<UUID>>> itemsRetention = wrapWithErrorHandling(
                retainItems(storeId, cartId),
                "Failed to retain items"
        );

        Mono<Result<UUID>> slotRetention = wrapWithErrorHandling(
                retainSlot(storeId, slotId),
                "Failed to retain slot"
        );


        return Mono.zip(itemsRetention, slotRetention)
                .doOnNext(this::logOperationResults);
    }

    private <T> Mono<Result<T>> wrapWithErrorHandling(Mono<T> operation, String errorMessage) {
        return operation
                .map(Result::success)
                .onErrorResume(e -> {
                    if (log.isDebugEnabled()) {
                        log.error(errorMessage, e);
                    } else {
                        log.error("{}: {}", errorMessage, e.getClass().getSimpleName());
                    }
                    return Mono.just(Result.failure(e));
                });
    }

    private void logOperationResults(Tuple2<Result<List<UUID>>, Result<UUID>> tuple) {
        log.info("Operations completed - itemsRetention: {}, slotRetention: {}",
                tuple.getT1().isSuccess(), tuple.getT2().isSuccess());

        if (tuple.getT1().isSuccess() && tuple.getT2().isSuccess()) {
            log.info("All operations successful - itemsRetainId: {}, slotRetainId: {}",
                    tuple.getT1().getValue(), tuple.getT2().getValue());
        }
    }

    private Mono<Order> processOperationResults(
            Tuple2<Result<List<UUID>>, Result<UUID>> retainResults, OrderRequest orderRequest,
            UUID userId) {

        OrderProcessingContext context = new OrderProcessingContext(retainResults, orderRequest,
                userId);

        return context.hasFailures()
                ? context.handleFailuresAndCleanup()
                : context.proceedWithOrderCreation();
    }

    // Inner class or separate class for better organization
    private class OrderProcessingContext {
        private final Tuple2<Result<List<UUID>>, Result<UUID>> retainResults;
        private final OrderRequest orderRequest;
        private final UUID userId;

        public OrderProcessingContext(Tuple2<Result<List<UUID>>, Result<UUID>> retainResults,
                                      OrderRequest orderRequest, UUID userId) {
            this.retainResults = retainResults;
            this.orderRequest = orderRequest;
            this.userId = userId;
        }

        public boolean hasFailures() {
            return !retainResults.getT1().isSuccess() ||
                    !retainResults.getT2().isSuccess();
        }

        public Mono<Order> handleFailuresAndCleanup() {
            List<Throwable> errors = collectErrors();
            Mono<Void> cleanup = releaseSuccessfulRetentions();
            return cleanup.then(Mono.error(Exceptions.multiple(errors)));
        }

        public Mono<Order> proceedWithOrderCreation() {
            return createOrderFromSuccessfulResults(retainResults, orderRequest, userId);
        }

        private List<Throwable> collectErrors() {
            List<Throwable> errors = new ArrayList<>();
            if (!retainResults.getT1().isSuccess()) errors.add(retainResults.getT1().getError());
            if (!retainResults.getT2().isSuccess()) errors.add(retainResults.getT2().getError());
            return errors;
        }

        private Mono<Void> releaseSuccessfulRetentions() {
            List<Mono<Void>> releaseOperations = new ArrayList<>();
            if (retainResults.getT1().isSuccess()) {
                releaseOperations.add(releaseItems(retainResults.getT1().getValue()));
            }
            if (retainResults.getT2().isSuccess()) {
                releaseOperations.add(releaseSlot(retainResults.getT2().getValue()));
            }
            return Mono.when(releaseOperations);
        }
    }

    private Mono<Order> createOrderFromSuccessfulResults(
            Tuple2<Result<List<UUID>>, Result<UUID>> results,
            OrderRequest orderRequest,
            UUID userId) {

        List<UUID> inventoryRetainIds = results.getT1().getValue();
        UUID slotRetainId = results.getT2().getValue();

        return Mono.fromSupplier(() -> createInitialOrderObject(userId, orderRequest.storeId(),
                        inventoryRetainIds, slotRetainId, orderRequest.slotId(),
                        orderRequest.billingAddress(), orderRequest.deliveryAddress()))
                .flatMap(this::calculateOrderTotals)
                .flatMap(this::saveOrder)
                .doOnSuccess(this::publishOrderCreatedEvent)
                .onErrorResume(e -> releaseResources(inventoryRetainIds, slotRetainId)
                        .then(Mono.error(e)));
    }


    /**
     * Checks availability based on delivery mode
     *
     * @return Mono<Boolean> indicating if delivery is available
     */
    private Mono<Boolean> checkAvailability(
            UUID storeId, UUID cartId, UUID slotId
    ) {
        return Mono.zip(
                checkItemsAvailability(storeId, cartId),
                checkSlotAvailability(storeId, slotId)
        ).map(tuple -> {
            boolean isItemsAvailable = tuple.getT1();
            boolean isSlotAvailable = tuple.getT2();
            return isItemsAvailable && isSlotAvailable;
        });
    }

    private Mono<Boolean> checkSlotAvailability(UUID storeId, UUID slotId) {
        return slotService.checkAvailability(storeId, slotId);
    }

    private Mono<Boolean> checkItemsAvailability(UUID storeId, UUID cartId) {
        return productService.checkAvailability(storeId, cartId);
    }

    /**
     * Retains the delivery slot with the id storeId
     *
     * @param slotId the slot id to retain
     * @return Mono<UUID> with the slot retention ID
     */
    private Mono<UUID> retainSlot(UUID storeId, UUID slotId) {
        // Implementation of slot retention logic
        return slotService.retain(storeId, slotId)
                .onErrorResume(e -> Mono.error(new Exception("Failed to retain slot" + e)));
    }

    private Mono<List<UUID>> retainItems(UUID storeId, UUID cartId) {
        return productService.retain(storeId, cartId)
                .onErrorResume(e -> Mono.error(new Exception("Failed to retain items")));
    }

    /**
     * Releases a previously retained delivery slot
     */
    private Mono<Void> releaseSlot(UUID retainId) {
        return slotService.release(retainId)
                .onErrorResume(e -> {
                    log.error("Failed to release delivery slot: {}", retainId, e);
                    return Mono.empty(); // Continue with the main error path even if release fails
                });
    }

    private Mono<Void> releaseItems(List<UUID> retainIds) {
        return productService.release(retainIds);
    }


    /**
     * Releases both inventory and slot resources in case of failure
     * (Release inventory and delivery slot) publish events to tell about release
     */
    private Mono<Void> releaseResources(List<UUID> inventoryRetainIds, UUID slotRetainId) {
        return Mono.when(
                releaseItems(inventoryRetainIds),
                releaseSlot(slotRetainId)
        );
    }


    private Mono<Order> calculateOrderTotals(Order order) {
        // Call external service to calculate all totals and discounts.
        return pricingService.calculateOrderTotals(order)
                .onErrorResume(e -> {
                    if (log.isDebugEnabled()) {
                        log.error("Error calculating order totals", e);
                    } else {
                        log.error("Error calculating order totals {}", e.getClass()
                                .getSimpleName());
                    }
                    return Mono.error(new OrderCalculationException("Failed to calculate order totals"));
                });
    }

    private Order createInitialOrderObject(UUID userId, UUID storeId, List<UUID> inventoryRetainIds,
                                           UUID slotRetainId, UUID slotId, String billingAddress,
                                           String deliveryAddress) {
        Order order = Order.builder()
                .userId(userId)
                .inventoryRetainIds(inventoryRetainIds)
                .slotRetainId(slotRetainId)
                .slotId(slotId)
                .storeId(storeId)
                .billingAddress(billingAddress)
                .deliveryAddress(deliveryAddress)
                .build();
        log.debug("Initializing order {}", order);

        return order;
    }


    private Mono<Order> saveOrder(Order order) {
        return orderRepository.save(order);
    }

    private void publishOrderCreatedEvent(Order order) {
        // On order created event published, the catalog service will add the order id to the
        // reservations, it will confirm reservation on payment success
        eventPublisher.publish(new OrderCreatedEvent(order));
    }

    public Mono<PaymentLink> placeOrderWithOnlinePayment(OrderRequest orderRequest) {
        return placeOrder(orderRequest)
                .flatMap((this::getPaymentApprovalLink))
                .onErrorResume(e -> Mono.error(new Exception("Failed to get payment approval link" + e)));
    }


    private Mono<PaymentLink> getPaymentApprovalLink(Order order) {
        return paymentService.getPaymentApprovalLink(order);
    }


}
