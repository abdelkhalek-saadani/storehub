package com.abdelkhalek.storehub.order.implementations;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryAdapterTest {

/*
    @Mock
    private OrderReactiveRepository orderReactiveRepository;

    @Mock
    private OrderEntityMapper orderEntityMapper;

    @InjectMocks
    private OrderRepositoryAdapter orderRepositoryAdapter;

    private OrderEntity orderEntity;
    private Order order;

    @BeforeEach
    void setUp() {

        order = createSampleOrder();
        orderEntity = createSampleOrderEntity();

        when(orderEntityMapper.fromOrder(order)).thenReturn(orderEntity);

    }

    @Test
    void save_shouldMapOrderToEntityAndSaveAndMapBack() {
        // Given

        OrderEntity savedOrderEntity = createSampleOrderEntity();
        Order savedOrder = createSampleOrder();

        when(orderReactiveRepository.save(orderEntity)).thenReturn(Mono.just(savedOrderEntity));
        when(orderEntityMapper.toOrder(savedOrderEntity)).thenReturn(savedOrder);

        // When
        Mono<Order> result = orderRepositoryAdapter.save(order);

        // Then
        StepVerifier.create(result)
                .expectNext(savedOrder)
                .verifyComplete();

        verify(orderEntityMapper).fromOrder(order);
        verify(orderReactiveRepository).save(orderEntity);
        verify(orderEntityMapper).toOrder(savedOrderEntity);
    }

    @Test
    void save_shouldHandleEmptyMono() {
        // Given
        when(orderReactiveRepository.save(orderEntity)).thenReturn(Mono.empty());

        // When
        Mono<Order> result = orderRepositoryAdapter.save(order);

        // Then
        StepVerifier.create(result)
                .verifyComplete();

        verify(orderEntityMapper).fromOrder(order);
        verify(orderReactiveRepository).save(orderEntity);
        verify(orderEntityMapper, never()).toOrder(any());
    }

    @Test
    void save_shouldHandleError() {
        // Given

        RuntimeException exception = new RuntimeException("Database error");

        when(orderReactiveRepository.save(orderEntity)).thenReturn(Mono.error(exception));

        // When
        Mono<Order> result = orderRepositoryAdapter.save(order);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex.equals(exception))
                .verify();

        verify(orderEntityMapper).fromOrder(order);
        verify(orderReactiveRepository).save(orderEntity);
        verify(orderEntityMapper, never()).toOrder(any());
    }

    private Order createSampleOrder() {
        // Create a sample Order order object
        // Note: This implementation might need to be adjusted based on the actual Order class structure
        Order order = new Order();
        // Set necessary properties

        return order;
    }

    private OrderEntity createSampleOrderEntity() {
        // Create a sample OrderEntity
        // Note: This implementation might need to be adjusted based on the actual OrderEntity class structure
        OrderEntity orderEntity = new OrderEntity();
        // Set necessary properties
        return orderEntity;
    }
*/
}
