package com.abdelkhalek.storehub.order.implementations.product;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
/*

    @Mock
    private ProductClient productClient;

    @Mock
    private CartItemRequestMapper cartItemRequestMapper;

    @Mock
    private StoreRequestMapper storeRequestMapper;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    private List<CartItemRequest> cartItems;
    private Store store;
    private List<CartItemRequest> cartItemRequests;
    private UUID storeId;
    private UUID retainId;

    @BeforeEach
    void setUp() {

        cartItems = List.of(new CartItemRequest(, new CartItemRequest()));
        UUID storeId = UUID.randomUUID();
        store = new Store(storeId);
        cartItemRequests = Arrays.asList(new CartItemRequest(), new CartItemRequest());
        retainId = UUID.randomUUID();

        lenient().when(cartItemRequestMapper.fromCartItems(cartItems)).thenReturn(cartItemRequests);
        lenient().when(storeRequestMapper.fromStore(store)).thenReturn(storeId);
    }

    @Test
    void checkAvailability_ShouldReturnResultFromProductClient() {
        // Arrange
        when(productClient.getAvailability(new AvailabilityRequest(cartItemRequests, storeId))).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = productServiceImpl.checkAvailability(new AvailabilityRequest(cartItems, store));

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
        Mono<Boolean> result = productServiceImpl.checkAvailability(cartItems, store);

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
        Mono<UUID> result = productServiceImpl.retain(cartItems, store);

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
        Mono<UUID> result = productServiceImpl.retain(cartItems, store);

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
        Mono<Void> result = productServiceImpl.release(retainId);

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
        Mono<Void> result = productServiceImpl.release(retainId);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(error -> error.equals(testException))
                .verify();

        verify(eventPublisher).publish(any(ItemsReleaseEvent.class));
    }
*/
}