package com.abdelkhalek.storehub.order.implementations.product;



import com.abdelkhalek.storehub.order.order.models.OrderItem;
import com.abdelkhalek.storehub.order.order.models.Store;
import com.abdelkhalek.storehub.order.order.spi.EventPublisher;
import com.abdelkhalek.storehub.order.order.service.ProductClient;
import com.abdelkhalek.storehub.order.order.service.ProductServiceAdapter;
import com.abdelkhalek.storehub.order.order.mapper.CartItemRequestMapper;
import com.abdelkhalek.storehub.order.infrastructure.mappers.StoreRequestMapper;
import com.abdelkhalek.storehub.order.order.dto.CartItemRequest;
import com.abdelkhalek.storehub.order.order.dto.ItemsReleaseEvent;
import com.abdelkhalek.storehub.order.infrastructure.models.StoreRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceAdapterTest {

    @Mock
    private ProductClient productClient;

    @Mock
    private CartItemRequestMapper cartItemRequestMapper;

    @Mock
    private StoreRequestMapper storeRequestMapper;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ProductServiceAdapter productServiceAdapter;

    private List<OrderItem> cartItems;
    private Store store;
    private List<CartItemRequest> cartItemRequests;
    private StoreRequest storeRequest;
    private UUID retainId;

    @BeforeEach
    void setUp() {

        cartItems = Arrays.asList(new OrderItem(), new OrderItem());
        UUID storeId = UUID.randomUUID();
        store = new Store(storeId);
        cartItemRequests = Arrays.asList(new CartItemRequest(), new CartItemRequest());
        storeRequest = new StoreRequest(storeId.toString());
        retainId = UUID.randomUUID();

        lenient().when(cartItemRequestMapper.fromCartItems(cartItems)).thenReturn(cartItemRequests);
        lenient().when(storeRequestMapper.fromStore(store)).thenReturn(storeRequest);
    }

    @Test
    void checkAvailability_ShouldReturnResultFromProductClient() {
        // Arrange
        when(productClient.getAvailability(cartItemRequests, storeRequest)).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = productServiceAdapter.checkAvailability(cartItems, store);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(cartItemRequestMapper).fromCartItems(cartItems);
        verify(storeRequestMapper).fromStore(store);
        verify(productClient).getAvailability(cartItemRequests, storeRequest);
    }

    @Test
    void checkAvailability_ShouldPropagateErrorFromProductClient() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test exception");
        when(productClient.getAvailability(cartItemRequests, storeRequest)).thenReturn(Mono.error(testException));

        // Act
        Mono<Boolean> result = productServiceAdapter.checkAvailability(cartItems, store);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error.equals(testException))
                .verify();

        verify(cartItemRequestMapper).fromCartItems(cartItems);
        verify(storeRequestMapper).fromStore(store);
        verify(productClient).getAvailability(cartItemRequests, storeRequest);
    }

    @Test
    void retain_ShouldReturnRetainIdFromProductClient() {
        // Arrange
        when(productClient.retain(cartItemRequests, storeRequest)).thenReturn(Mono.just(retainId));

        // Act
        Mono<UUID> result = productServiceAdapter.retain(cartItems, store);

        // Assert
        StepVerifier.create(result)
                .expectNext(retainId)
                .verifyComplete();

        verify(cartItemRequestMapper).fromCartItems(cartItems);
        verify(storeRequestMapper).fromStore(store);
        verify(productClient).retain(cartItemRequests, storeRequest);
    }

    @Test
    void retain_ShouldPropagateErrorFromProductClient() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test exception");
        when(productClient.retain(cartItemRequests, storeRequest)).thenReturn(Mono.error(testException));

        // Act
        Mono<UUID> result = productServiceAdapter.retain(cartItems, store);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error.equals(testException))
                .verify();

        verify(cartItemRequestMapper).fromCartItems(cartItems);
        verify(storeRequestMapper).fromStore(store);
        verify(productClient).retain(cartItemRequests, storeRequest);
    }

    @Test
    void release_ShouldPublishEventWithRetainId() {
        // Arrange
        doNothing().when(eventPublisher).publish(any(ItemsReleaseEvent.class));
        // Act
        Mono<Void> result = productServiceAdapter.release(retainId);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();

        verify(eventPublisher).publish(argThat(event ->
                event instanceof ItemsReleaseEvent &&
                        ((ItemsReleaseEvent) event).getRetainId().equals(retainId.toString())
        ));
    }

    @Test
    void release_ShouldPropagateErrorFromEventPublisher() {
        // Arrange
        RuntimeException testException = new RuntimeException("Test exception");
        doThrow(testException).when(eventPublisher).publish(any(ItemsReleaseEvent.class));

        // Act
        Mono<Void> result = productServiceAdapter.release(retainId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error.equals(testException))
                .verify();

        verify(eventPublisher).publish(any(ItemsReleaseEvent.class));
    }
}