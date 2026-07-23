package com.abdelkhalek.storehub.order.implementations.pricing;

/*@ExtendWith(MockitoExtension.class)
*//**//*class PricingServiceImplTest {

    @Mock
    private PricingClient pricingClient;

    @Mock
    private PriceRequestMapper priceRequestMapper;

    @Mock
    private CartItemRequestMapper cartItemRequestMapper;

    @InjectMocks
    private PricingServiceImpl pricingServiceImpl;

    // Test data
    private Order testOrder;
    private PriceRequest testPriceRequest;
    private PriceResponse testPriceResponse;
    private List<OrderItem> responseCartItems;

    @BeforeEach
    void setUp() {
        // Create test order with cart items
        testOrder = createTestOrder();

        // Create test price request
        testPriceRequest = createTestPriceRequest();

        // Create test price response
        //testPriceResponse = createTestPriceResponse();

        // Create response cart items
        //responseCartItems = createResponseCartItems();

        // Configure mocks
        lenient().when(priceRequestMapper.fromOrder(testOrder)).thenReturn(testPriceRequest);
        lenient().when(cartItemRequestMapper.toCartItems(testPriceResponse.getItems())).thenReturn(responseCartItems);
    }

    @Test
    void calculateOrderTotals_Success() {
        // Arrange
        when(pricingClient.calculateOrderTotals(testPriceRequest))
                .thenReturn(Mono.just(testPriceResponse));

        // Act
        Mono<Order> result = pricingServiceImpl.calculateOrderTotals(testOrder);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(this::verifyOrderUpdated)
                .verifyComplete();

        // Verify interactions
        verify(priceRequestMapper).fromOrder(testOrder);
        verify(pricingClient).calculateOrderTotals(testPriceRequest);
        verify(cartItemRequestMapper).toCartItems(testPriceResponse.getItems());
    }

    @Test
    void calculateOrderTotals_ClientError() {
        // Arrange
        RuntimeException exception = new RuntimeException("Pricing service unavailable");
        when(pricingClient.calculateOrderTotals(testPriceRequest))
                .thenReturn(Mono.error(exception));

        // Act
        Mono<Order> result = pricingServiceImpl.calculateOrderTotals(testOrder);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(ex -> ex.equals(exception))
                .verify();

        // Verify interactions
        verify(priceRequestMapper).fromOrder(testOrder);
        verify(pricingClient).calculateOrderTotals(testPriceRequest);
        verifyNoInteractions(cartItemRequestMapper); // Should not be called on error
    }

    @Test
    void calculateOrderTotals_EmptyOrder() {
        // Arrange
        Order emptyOrder = new Order();
        emptyOrder.setId(UUID.randomUUID());
        emptyOrder.setCartItems(List.of());

        PriceRequest emptyRequest = new PriceRequest();
        emptyRequest.setItems(List.of());

        PriceResponse emptyResponse = new PriceResponse();
        emptyResponse.setItems(List.of());
        emptyResponse.setTotal(new Money(BigDecimal.ZERO));

        when(priceRequestMapper.fromOrder(emptyOrder)).thenReturn(emptyRequest);
        when(pricingClient.calculateOrderTotals(emptyRequest)).thenReturn(Mono.just(emptyResponse));
        when(cartItemRequestMapper.toCartItems(emptyResponse.getItems())).thenReturn(List.of());

        // Act
        Mono<Order> result = pricingServiceImpl.calculateOrderTotals(emptyOrder);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(order ->
                        order.getId().equals(emptyOrder.getId()) &&
                                order.getCartItems().isEmpty() &&
                                order.getTotal() != null &&
                                order.getTotal().getValue().equals(BigDecimal.ZERO)
                )
                .verifyComplete();
    }

    // Helper methods to create test data
    private Order createTestOrder() {
        Order order = new Order();
        order.setId(UUID.randomUUID());

        OrderItem cartItem1 = new OrderItem();
        cartItem1.setProductId(UUID.randomUUID());
        cartItem1.setQuantity(1);

        OrderItem cartItem2 = new OrderItem();
        cartItem2.setProductId(UUID.randomUUID());
        cartItem2.setQuantity(10);

        order.setCartItems(List.of(cartItem1, cartItem2));
        return order;
    }*/

/*
    private PriceRequest createTestPriceRequest() {
        CartItemRequest request1 = new CartItemRequest();
        request1.setProductId(testOrder.getCartItems().get(0).getProductId().toString());
        request1.setQuantity(testOrder.getCartItems().get(0).getQuantity());

        CartItemRequest request2 = new CartItemRequest();
        request2.setProductId(testOrder.getCartItems().get(1).getProductId().toString());
        request2.setQuantity(testOrder.getCartItems().get(1).getQuantity());

        PriceRequest priceRequest = new PriceRequest();
        priceRequest.setItems(List.of(request1, request2));
        return priceRequest;
    }

*/
    /*private PriceResponse createTestPriceResponse() {
        CartItemResponse response1 = new CartItemResponse();
        response1.setProductId(testOrder.getCartItems().get(0).getProductId().toString());
        response1.setQuantity(testOrder.getCartItems().get(0).getQuantity());
        response1.setOriginalUnitPrice(BigDecimal.valueOf(15));
        response1.setUnitPrice(BigDecimal.TEN);
        response1.setSubtotal(BigDecimal.TEN);

        CartItemResponse response2 = new CartItemResponse();
        response2.setProductId(testOrder.getCartItems().get(1).getProductId().toString());
        response2.setQuantity(testOrder.getCartItems().get(1).getQuantity());
        response2.setOriginalUnitPrice(new Money(BigDecimal.TEN));
        response2.setUnitPrice(new Money(BigDecimal.TEN));
        response2.setSubtotal(new Money(BigDecimal.valueOf(100)));

        PriceResponse priceResponse = new PriceResponse();
        priceResponse.setItems(List.of(response1, response2));
        priceResponse.setTotal(new Money(BigDecimal.valueOf(110)));
        return priceResponse;
    }*/

    //private List<OrderItem> createResponseCartI/**/tems() /*{
      /*  OrderItem item1 = new OrderItem(
                testOrder.getCartItems().get(0).getProductId(),
                testPriceResponse.getItems().get(0).getQuantity(),
                testPriceResponse.getItems().get(0).getSubtotal(),
                testPriceResponse.getItems().get(0).getUnitPrice(),
                testPriceResponse.getItems().get(0).getOriginalUnitPrice()
        );

        OrderItem item2 = new OrderItem(
                testOrder.getCartItems().get(1).getProductId(),
                testPriceResponse.getItems().get(1).getQuantity(),
                testPriceResponse.getItems().get(1).getSubtotal(),
                testPriceResponse.getItems().get(1).getUnitPrice(),
                testPriceResponse.getItems().get(1).getOriginalUnitPrice()
        );

        return List.of(item1, item2);
    }*/

/**//*
    private boolean verifyOrderUpdated(Order updatedOrder) {
*/
/*
        // Verify order ID remains unchanged
        if (!updatedOrder.getId().equals(testOrder.getId())) {
            return false;
        }

        // Verify cart items were updated correctly
*//*

*/
/*
        if (updatedOrder.getCartItems().size() != responseCartItems.size()) {
            return false;
        }
*//*
*/
/*


        // Verify total was set correctly
   *//*

*/
/*     return updatedOrder.getTotal() != null &&
                updatedOrder.getTotal().getValue().equals(testPriceResponse.getTotal().getValue()) &&
                updatedOrder.getCartItems().equals(responseCartItems);
    }*//*
*/
/*

}*/
