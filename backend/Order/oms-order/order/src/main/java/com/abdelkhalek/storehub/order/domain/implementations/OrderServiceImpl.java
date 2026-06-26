package com.abdelkhalek.storehub.order.domain.implementations;

import com.abdelkhalek.storehub.order.domain.CouponService;
import com.abdelkhalek.storehub.order.domain.OrderService;
import com.abdelkhalek.storehub.order.domain.exceptions.OrderCalculationException;
import com.abdelkhalek.storehub.order.domain.exceptions.UnavailableException;
import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.domain.spi.*;
import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.domain.spi.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {
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
    private final CouponService couponService;
    private final OrderRepository orderRepository;
    private final EventPublisher eventPublisher;
    private final PaymentService paymentService;


    @Override
    public Mono<Order> placeOrderWithCashPayment(Cart cart, Delivery delivery, Slot slot, Coupon coupon, Store store) {
        return placeOrder(cart, delivery, slot, coupon, store, PaymentMode.CASH);
    }

    private Mono<Order> placeOrder(Cart cart, Delivery delivery, Slot slot,
                                   Coupon coupon, Store store, PaymentMode paymentMode) {
        // First check if order is valid based on delivery mode, store, items availability and slot
        return checkAvailability(cart.getItems(), delivery, slot, store, coupon)
                .flatMap(isAvailable -> {
                    if (!isAvailable) {
                        return Mono.error(new UnavailableException("Unavailable items, slot or delivery mode in that store"));
                    }

                    return processAvailableOrder(cart, delivery, slot, coupon, store, paymentMode);
                });
    }


    /**
     * Processes an order after availability is confirmed
     * Handles inventory, slot retention, fee calculation, order creation, and persistence
     */
    private Mono<Order>     processAvailableOrder(Cart cart, Delivery delivery, Slot slot,
                                              Coupon coupon, Store store, PaymentMode paymentMode) {
        return executeParallelOperations(cart, delivery, slot, store)
                .flatMap(results -> processOperationResults(results, cart, delivery, slot, coupon, paymentMode));
    }

    private Mono<Tuple3<Result<UUID>, Result<UUID>, Result<Money>>> executeParallelOperations(
            Cart cart, Delivery delivery, Slot slot, Store store) {

        Mono<Result<UUID>> itemsRetention = wrapWithErrorHandling(
                retainItems(cart.getItems(), store),
                "Failed to retain items"
        );

        Mono<Result<UUID>> slotRetention = wrapWithErrorHandling(
                retainSlot(delivery, slot, store),
                "Failed to retain slot"
        );

        Mono<Result<Money>> feeCalculation = wrapWithErrorHandling(
                calculateDeliveryFee(delivery, cart, store),
                "Failed to calculate delivery fee"
        );

        return Mono.zip(itemsRetention, slotRetention, feeCalculation)
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

    private void logOperationResults(Tuple3<Result<UUID>, Result<UUID>, Result<Money>> tuple) {
        log.info("Operations completed - itemsRetention: {}, slotRetention: {}, feeCalculation: {}",
                tuple.getT1().isSuccess(), tuple.getT2().isSuccess(), tuple.getT3().isSuccess());

        if (tuple.getT1().isSuccess() && tuple.getT2().isSuccess() && tuple.getT3().isSuccess()) {
            log.info("All operations successful - itemsRetainId: {}, slotRetainId: {}, deliveryFee: {}",
                    tuple.getT1().getValue(), tuple.getT2().getValue(), tuple.getT3().getValue());
        }
    }

    private Mono<Order> processOperationResults(
            Tuple3<Result<UUID>, Result<UUID>, Result<Money>> results,
            Cart cart, Delivery delivery, Slot slot, Coupon coupon, PaymentMode paymentMode) {

        OrderProcessingContext context = new OrderProcessingContext(results, cart, delivery, slot, coupon, paymentMode);

        return context.hasFailures()
                ? context.handleFailuresAndCleanup()
                : context.proceedWithOrderCreation();
    }

    // Inner class or separate class for better organization
    private class OrderProcessingContext {
        private final Tuple3<Result<UUID>, Result<UUID>, Result<Money>> results;
        private final Cart cart;
        private final Delivery delivery;
        private final Slot slot;
        private final Coupon coupon;
        private final PaymentMode paymentMode;

        public OrderProcessingContext(Tuple3<Result<UUID>, Result<UUID>, Result<Money>> results,
                                      Cart cart, Delivery delivery, Slot slot, Coupon coupon, PaymentMode paymentMode) {
            this.results = results;
            this.cart = cart;
            this.delivery = delivery;
            this.slot = slot;
            this.coupon = coupon;
            this.paymentMode = paymentMode;
        }

        public boolean hasFailures() {
            return !results.getT1().isSuccess() ||
                    !results.getT2().isSuccess() ||
                    !results.getT3().isSuccess();
        }

        public Mono<Order> handleFailuresAndCleanup() {
            List<Throwable> errors = collectErrors();
            Mono<Void> cleanup = releaseSuccessfulRetentions();
            return cleanup.then(Mono.error(Exceptions.multiple(errors)));
        }

        public Mono<Order> proceedWithOrderCreation() {
            return createOrderFromSuccessfulResults(results, cart, delivery, slot, coupon, paymentMode);
        }

        private List<Throwable> collectErrors() {
            List<Throwable> errors = new ArrayList<>();
            if (!results.getT1().isSuccess()) errors.add(results.getT1().getError());
            if (!results.getT2().isSuccess()) errors.add(results.getT2().getError());
            if (!results.getT3().isSuccess()) errors.add(results.getT3().getError());
            return errors;
        }

        private Mono<Void> releaseSuccessfulRetentions() {
            List<Mono<Void>> releaseOperations = new ArrayList<>();
            if (results.getT1().isSuccess()) {
                releaseOperations.add(releaseItems(results.getT1().getValue()));
            }
            if (results.getT2().isSuccess()) {
                releaseOperations.add(releaseSlot(results.getT2().getValue()));
            }
            return Mono.when(releaseOperations);
        }
    }

    private Mono<Order> createOrderFromSuccessfulResults(
            Tuple3<Result<UUID>, Result<UUID>, Result<Money>> results,
            Cart cart, Delivery delivery, Slot slot, Coupon coupon, PaymentMode paymentMode) {

        UUID inventoryRetainId = results.getT1().getValue();
        UUID slotRetainId = results.getT2().getValue();
        Money deliveryFee = results.getT3().getValue();

        return Mono.fromSupplier(() -> createInitialOrderObject(cart, delivery, slot, deliveryFee,
                        inventoryRetainId, slotRetainId, paymentMode))
                .flatMap(this::calculateOrderTotals)
                .flatMap(order -> applyCoupon(order, coupon))
                .flatMap(this::saveOrder)
                .doOnSuccess(this::publishOrderCreatedEvent)
                .onErrorResume(e -> releaseResources(inventoryRetainId, slotRetainId, coupon)
                        .then(Mono.error(e)));
    }


    /**
     * Checks availability based on delivery mode
     *
     * @return Mono<Boolean> indicating if delivery is available
     */
    private Mono<Boolean> checkAvailability(List<CartItem> items, Delivery delivery, Slot slot, Store store, Coupon coupon) {
        // Different availability check logic based on delivery mode
        // NOTE: think about merging both in a method called check and inside it the check on the method and call of checkStore or customer depending on the delivery mode
        return Mono.zip(
                checkItemsAvailability(items, store),
                checkSlotAvailability(slot, store, delivery),
                checkCoupon(coupon)
        ).map(tuple -> {
            boolean isItemsAvailable = tuple.getT1();
            boolean isSlotAvailable = tuple.getT2();
            boolean isCouponValid = tuple.getT3();
            return isItemsAvailable && isSlotAvailable && isCouponValid;
        });
    }

    private Mono<Boolean> checkSlotAvailability(Slot slot, Store store, Delivery delivery) {
        return slotService.checkAvailability(delivery, slot, store);
    }

    private Mono<Boolean> checkItemsAvailability(List<CartItem> items, Store store) {
        return productService.checkAvailability(items, store);
    }

    /**
     * Retains a delivery slot for the given delivery and time slot
     *
     * @return Mono<UUID> with the slot retention ID
     */
    private Mono<UUID> retainSlot(Delivery delivery, Slot slot, Store store) {
        // Implementation of slot retention logic
        return slotService.retain(delivery, slot, store)
                .onErrorResume(e -> Mono.error(new Exception("Failed to retain slot" + e)));
    }

    private Mono<UUID> retainItems(List<CartItem> items, Store store) {
        return productService.retain(items, store)
                .onErrorResume(e -> Mono.error(new Exception("Failed to retain items" )));
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

    private Mono<Void> releaseItems(UUID retainId) {
        return productService.release(retainId);
    }

    private Mono<Void> releaseCoupon(Coupon coupon) {
        return couponService.markCouponUnused(coupon);
    }


    /**
     * Release any kind of resource
     * Releases both inventory and slot resources in case of failure
     * (Release inventory and delivery slot) publish events to tell about release
     */
    private Mono<Void> releaseResources(UUID inventoryRetainId, UUID slotRetainId, Coupon coupon) {
        return Mono.when(
                releaseItems(inventoryRetainId),
                releaseSlot(slotRetainId), // same topic or different topics , what is correlation id
                releaseCoupon(coupon)
        );
    }


    /**
     * Calculates delivery fee based on delivery mode
     *
     * @return Mono<Money> with the fee amount or zero for pickup
     */
    private Mono<Money> calculateDeliveryFee(Delivery delivery, Cart cart, Store store) {
        return delivery.getMode().equals(DeliveryMode.HOME_DELIVERY) ?
                getDeliveryFee(cart.getItems(), delivery.getAddress(), store) :
                Mono.just(new Money()); // No fee for pickup
    }


    private Mono<Money> getDeliveryFee(List<CartItem> items, Address customerAddress, Store store) {
        return Mono.fromSupplier(() -> new Money(BigDecimal.TEN));
    }


    private Mono<Order> calculateOrderTotals(Order order) {
        // Call external service to calculate all totals and discounts.
        return pricingService.calculateOrderTotals(order)
                .onErrorResume(e -> {
                    if (log.isDebugEnabled()) {
                        log.error("Error calculating order totals", e);
                    } else {
                        log.error("Error calculating order totals {}", e.getClass().getSimpleName());
                    }
                    return Mono.error(new OrderCalculationException("Failed to calculate order totals"));
                });
    }

    private Order createInitialOrderObject(Cart cart, Delivery delivery, Slot slot, Money deliveryFee, UUID inventoryRetainId, UUID slotRetainId, PaymentMode paymentMode) {
        Order order = new Order();

        order.setDate(LocalDateTime.now());
        order.setDeliveryAddress(delivery.getAddress());
        order.setDeliveryFee(deliveryFee);
        order.setSlotRetainId(slotRetainId);
        order.setInventoryRetainId(inventoryRetainId);
        order.setSlot(slot);
        order.setCartItems(cart.getItems());
        order.setPaymentMode(paymentMode);

        return order;
    }

    private Mono<Boolean> checkCoupon(Coupon coupon) {
        return couponService.validateCoupon(coupon);
    }

    private Mono<Order> applyCoupon(Order order, Coupon coupon) {
        if (coupon == null) {
            return Mono.just(order);
        }

        return couponService.applyCoupon(order, coupon);  // validate coupon include marking it as "used for the order with id" in the coupon microservice (this sevice contains external call)

        // this will just apply the coupon, later will look if the calculation happens here or in the coupon microservice


    }

    private Mono<Order> saveOrder(Order order) {
        return orderRepository.save(order);
    }

    private void publishOrderCreatedEvent(Order order) {
        eventPublisher.publish(new OrderCreatedEvent(order));
    }

    @Override
    public Mono<PaymentLink> placeOrderWithOnlinePayment(Cart cart, Delivery delivery, Slot slot, Coupon coupon, Store store) {
        return placeOrder(cart, delivery, slot, coupon, store, PaymentMode.PAYPAL)
                .flatMap((this::getPaymentApprovalLink))
                .onErrorResume(e -> Mono.error(new Exception("Failed to get payment approval link" + e)));
    }

    public Mono<PaymentLink> getPaymentApprovalLinkById(UUID orderId) {
        return getPaymentApprovalLink(new Order(orderId));
    }

    private Mono<PaymentLink> getPaymentApprovalLink(Order order) {
        return paymentService.getPaymentApprovalLink(order);
    }


}
