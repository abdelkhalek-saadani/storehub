package com.abdelkhalek.storehub.order.domain;

import com.abdelkhalek.storehub.order.domain.exceptions.OrderCalculationException;
import com.abdelkhalek.storehub.order.domain.exceptions.UnavailableException;
import com.abdelkhalek.storehub.order.domain.implementations.OrderServiceImpl;
import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.domain.spi.*;
import com.abdelkhalek.storehub.order.domain.models.*;
import com.abdelkhalek.storehub.order.domain.spi.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import reactor.core.Exceptions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private SlotService slotService;

    @Mock
    private ProductService productService;

    @Mock
    private PricingService pricingService;

    @Mock
    private CouponService couponService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Cart cart;
    private Delivery delivery;
    private Slot slot;
    private Coupon coupon;
    private Store store;
    private List<CartItem> cartItems;
    private UUID inventoryRetainId;
    private UUID slotRetainId;

    @BeforeEach
    void setUp() {

        // Setup test data
        cart = new Cart();
        CartItem item1 = new CartItem(
                UUID.randomUUID(),
                5,
                new Money(BigDecimal.valueOf(50)),
                new Money(BigDecimal.TEN),
                new Money(BigDecimal.TEN));
        CartItem item2 = new CartItem(
                UUID.randomUUID(),
                10,
                new Money(BigDecimal.valueOf(20)),
                new Money(BigDecimal.TWO),
                new Money(BigDecimal.TWO));
        cart.setItems(List.of(item1, item2));

        delivery = new Delivery();
        delivery.setAddress(Address.getDefaultAddress());
        delivery.setMode(DeliveryMode.PICKUP);

        slot = Slot.getDefaultSlot();

        coupon = new Coupon("some-code");

        store = new Store(UUID.randomUUID());

        inventoryRetainId = UUID.randomUUID();
        slotRetainId = UUID.randomUUID();
    }

    @Test
    void placeOrderWithCashPayment_Success() {
        // Arrange
        System.out.println("the cart is " + cart);
        Order expectedOrder = new Order();
        expectedOrder.setId(UUID.randomUUID());

        // Mock all success cases
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        when(productService.retain(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(inventoryRetainId));
        when(slotService.retain(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(slotRetainId));

        when(pricingService.calculateOrderTotals(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setTotal(new Money(BigDecimal.valueOf(100)));
            return Mono.just(order);
        });

        when(couponService.applyCoupon(any(Order.class), eq(coupon))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setTotal(new Money(order.getTotal().getValue().subtract(BigDecimal.TEN)));
            return Mono.just(order);
        });

        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(expectedOrder));

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectNext(expectedOrder)
                .verifyComplete();

        // Verify all methods were called
        verify(productService).checkAvailability(eq(cart.getItems()), eq(store));
        verify(slotService).checkAvailability(eq(delivery), eq(slot), eq(store));
        verify(couponService).validateCoupon(eq(coupon));
        verify(productService).retain(eq(cart.getItems()), eq(store));
        verify(slotService).retain(eq(delivery), eq(slot), eq(store));
        verify(pricingService).calculateOrderTotals(any(Order.class));
        verify(couponService).applyCoupon(any(Order.class), eq(coupon));
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any(OrderCreatedEvent.class));

        // Verify no releases were called
        verify(productService, never()).release(any(UUID.class));
        verify(slotService, never()).release(any(UUID.class));
        verify(couponService, never()).markCouponUnused(any(Coupon.class));
    }

    @Test
    void placeOrderWithCashPayment_FailOnItemsRetention() {
        // Arrange
        Exception retentionException = new Exception("Failed to retain items from test");

        // Mock availability checks to succeed
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        // Mock product retention to fail
        when(productService.retain(eq(cart.getItems()), eq(store))).thenReturn(Mono.error(retentionException));

        // Mock slot retention to succeed (to test that it gets released on error)
        when(slotService.retain(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(slotRetainId));

        // Mock resource releases
//        when(slotService.release(any(UUID.class))).thenReturn(Mono.empty());
        when(slotService.release(any(UUID.class))).thenReturn(Mono.empty());


        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectErrorMatches(e -> {
                    List<Throwable> errors = Exceptions.unwrapMultiple(e);
                    return errors.stream().anyMatch(err -> err.getMessage().contains("Failed to retain items"));
                })
                .verify();

        // Verify availability checks were called
        verify(productService).checkAvailability(eq(cart.getItems()), eq(store));
        verify(slotService).checkAvailability(eq(delivery), eq(slot), eq(store));
        verify(couponService).validateCoupon(eq(coupon));

        // Verify retentions were attempted
        verify(productService).retain(eq(cart.getItems()), eq(store));
        verify(slotService).retain(eq(delivery), eq(slot), eq(store));

        // Verify no order was saved
        verify(pricingService, never()).calculateOrderTotals(any(Order.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publish(any(OrderCreatedEvent.class));

        // Since the error happens in the first retention operation (items), 
        // The first retention should be released
        verify(slotService).release(any(UUID.class));
        // no resources should need to be released
        verify(productService, never()).release(any(UUID.class));
        verify(couponService, never()).markCouponUnused(any(Coupon.class));
    }

    @Test
    void placeOrderWithCashPayment_FailOnSlotRetention() {
        // Arrange
        Exception retentionException = new Exception("Failed to retain slot");

        // Mock availability checks to succeed
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        // Mock product retention to succeed
        when(productService.retain(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(inventoryRetainId));

        // Mock slot retention to fail
        when(slotService.retain(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.error(retentionException));

        // Mock resource releases
        when(productService.release(eq(inventoryRetainId))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectErrorMatches(e -> {
                    List<Throwable> errors = Exceptions.unwrapMultiple(e);
                    return errors.stream().anyMatch(err -> err.getMessage().contains("Failed to retain slot"));
                })
                .verify();

        // Verify availability checks were called
        verify(productService).checkAvailability(eq(cart.getItems()), eq(store));
        verify(slotService).checkAvailability(eq(delivery), eq(slot), eq(store));
        verify(couponService).validateCoupon(eq(coupon));

        // Verify retentions were attempted
        verify(productService).retain(eq(cart.getItems()), eq(store));
        verify(slotService).retain(eq(delivery), eq(slot), eq(store));

        // Verify no order was saved
        verify(pricingService, never()).calculateOrderTotals(any(Order.class));
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publish(any(OrderCreatedEvent.class));

        // Verify the successfully retained resources were released
        verify(productService).release(eq(inventoryRetainId));
        verify(slotService, never()).release(any(UUID.class)); // Slot was never retained
        verify(couponService, never()).markCouponUnused(any(Coupon.class));
    }

    @Test
    void placeOrderWithCashPayment_FailOnCouponValidation() {
        // Arrange
        // Mock item and slot availability to succeed, but coupon validation to fail
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectErrorMatches(e -> e instanceof UnavailableException &&
                        e.getMessage().contains("Unavailable"))
                .verify();

        // Verify availability checks were called
        verify(productService).checkAvailability(eq(cart.getItems()), eq(store));
        verify(slotService).checkAvailability(eq(delivery), eq(slot), eq(store));
        verify(couponService).validateCoupon(eq(coupon));

        // Verify no retentions, calculations, or saves were attempted
        verify(productService, never()).retain(any(), any());
        verify(slotService, never()).retain(any(), any(), any());
        verify(pricingService, never()).calculateOrderTotals(any());
        verify(orderRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());

        // Verify no releases were needed since nothing was retained
        verify(productService, never()).release(any());
        verify(slotService, never()).release(any());
        verify(couponService, never()).markCouponUnused(any());
    }

    @Test
    void placeOrderWithCashPayment_FailOnCalculateOrderTotals() {
        // Arrange
        OrderCalculationException calculationException = new OrderCalculationException("Failed to calculate totals");

        // Mock all availability checks to succeed
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        // Mock all retentions to succeed
        when(productService.retain(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(inventoryRetainId));
        when(slotService.retain(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(slotRetainId));

        // Mock order total calculation to fail
        when(pricingService.calculateOrderTotals(any(Order.class))).thenReturn(Mono.error(calculationException));

        // Mock resource releases
        when(productService.release(eq(inventoryRetainId))).thenReturn(Mono.empty());
        when(slotService.release(eq(slotRetainId))).thenReturn(Mono.empty());
        when(couponService.markCouponUnused(eq(coupon))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectError(OrderCalculationException.class)
                .verify();

        // Verify availability checks and retentions were called
        verify(productService).checkAvailability(eq(cart.getItems()), eq(store));
        verify(slotService).checkAvailability(eq(delivery), eq(slot), eq(store));
        verify(couponService).validateCoupon(eq(coupon));
        verify(productService).retain(eq(cart.getItems()), eq(store));
        verify(slotService).retain(eq(delivery), eq(slot), eq(store));
        verify(pricingService).calculateOrderTotals(any(Order.class));

        // Verify no order was saved
        verify(orderRepository, never()).save(any(Order.class));
        verify(eventPublisher, never()).publish(any(OrderCreatedEvent.class));

        // Verify all retained resources were released
        verify(productService).release(eq(inventoryRetainId));
        verify(slotService).release(eq(slotRetainId));
        verify(couponService).markCouponUnused(eq(coupon));
    }

    @Test
    void placeOrderWithCashPayment_MultipleErrors() {
        // Arrange - simulate errors in both retentions
        Exception itemsException = new Exception("Failed to retain items");
        Exception slotException = new Exception("Failed to retain slot");

        // Mock availability checks to succeed
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        // Mock both retentions to fail
        when(productService.retain(eq(cart.getItems()), eq(store))).thenReturn(Mono.error(itemsException));
        when(slotService.retain(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.error(slotException));

        // Act & Assert - should get combined error
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectErrorMatches(e -> {
                    List<Throwable> errors = Exceptions.unwrapMultiple(e);
                        return errors.get(0).getMessage().contains("Failed to retain items") && errors.get(1).getMessage().contains("Failed to retain slot");
                })
                .verify();

        // Verify no resources needed to be released since nothing was successfully retained
        verify(productService, never()).release(any(UUID.class));
        verify(slotService, never()).release(any(UUID.class));
        verify(couponService, never()).markCouponUnused(any(Coupon.class));
    }

    @Test
    void placeOrderWithCashPayment_ItemsUnavailable() {
        // Arrange
        // Mock item availability to fail
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(false));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectErrorMatches(e -> e instanceof UnavailableException &&
                        e.getMessage().contains("Unavailable"))
                .verify();

        // Verify availability checks were called
        verify(productService).checkAvailability(eq(cart.getItems()), eq(store));
        verify(slotService).checkAvailability(eq(delivery), eq(slot), eq(store));
        verify(couponService).validateCoupon(eq(coupon));

        // Verify no further processing
        verify(productService, never()).retain(any(), any());
        verify(slotService, never()).retain(any(), any(), any());
    }

    @Test
    void placeOrderWithCashPayment_SlotUnavailable() {
        // Arrange
        // Mock slot availability to fail
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(false));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectErrorMatches(e -> e instanceof UnavailableException &&
                        e.getMessage().contains("Unavailable"))
                .verify();

        // Verify no further processing
        verify(productService, never()).retain(any(), any());
        verify(slotService, never()).retain(any(), any(), any());
    }

    @Test
    void placeOrderWithCashPayment_ErrorDuringCouponApplication() {
        // Arrange
        Exception couponException = new Exception("Failed to apply coupon");

        // Mock all availability checks to succeed
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(coupon))).thenReturn(Mono.just(true));

        // Mock all retentions to succeed
        when(productService.retain(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(inventoryRetainId));
        when(slotService.retain(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(slotRetainId));

        // Mock order total calculation to succeed
        when(pricingService.calculateOrderTotals(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setTotal(new Money(BigDecimal.valueOf(100)));
            return Mono.just(order);
        });

        // Mock coupon application to fail
        when(couponService.applyCoupon(any(Order.class), eq(coupon))).thenReturn(Mono.error(couponException));

        // Mock resource releases
        when(productService.release(eq(inventoryRetainId))).thenReturn(Mono.empty());
        when(slotService.release(eq(slotRetainId))).thenReturn(Mono.empty());
        when(couponService.markCouponUnused(eq(coupon))).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, coupon, store))
                .expectErrorMatches(e -> e.getMessage().contains("Failed to apply coupon"))
                .verify();

        // Verify all retained resources were released
        verify(productService).release(eq(inventoryRetainId));
        verify(slotService).release(eq(slotRetainId));
        verify(couponService).markCouponUnused(eq(coupon));

        // Verify no order was saved
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrderWithCashPayment_NullCoupon() {
        // Arrange
        Coupon nullCoupon = null;
        Order expectedOrder = new Order();
        expectedOrder.setId(UUID.randomUUID());

        // Mock all success cases
        when(productService.checkAvailability(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(true));
        when(slotService.checkAvailability(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(true));
        when(couponService.validateCoupon(eq(nullCoupon))).thenReturn(Mono.just(true));

        when(productService.retain(eq(cart.getItems()), eq(store))).thenReturn(Mono.just(inventoryRetainId));
        when(slotService.retain(eq(delivery), eq(slot), eq(store))).thenReturn(Mono.just(slotRetainId));

        when(pricingService.calculateOrderTotals(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setTotal(new Money(BigDecimal.valueOf(100)));
            return Mono.just(order);
        });

        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(expectedOrder));

        // Act & Assert
        StepVerifier.create(orderService.placeOrderWithCashPayment(cart, delivery, slot, nullCoupon, store))
                .expectNext(expectedOrder)
                .verifyComplete();

        // Verify coupon application was not attempted
        verify(couponService, never()).applyCoupon(any(Order.class), any(Coupon.class));
    }



}