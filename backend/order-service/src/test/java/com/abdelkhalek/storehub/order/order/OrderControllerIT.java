package com.abdelkhalek.storehub.order.order;

import com.abdelkhalek.storehub.order.cart.domain.CartOwner;
import com.abdelkhalek.storehub.order.cart.entity.CartEntity;
import com.abdelkhalek.storehub.order.cart.entity.CartItemEntity;
import com.abdelkhalek.storehub.order.cart.repository.CartRepository;
import com.abdelkhalek.storehub.order.order.dto.OrderCreatedResponse;
import com.abdelkhalek.storehub.order.order.dto.OrderRequest;
import com.abdelkhalek.storehub.order.order.models.Order;
import com.abdelkhalek.storehub.order.order.models.OrderStatus;
import com.abdelkhalek.storehub.order.order.spi.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureWireMock(port = 12345)
class OrderControllerIT {
    static final int WIREMOCK_PORT = 12345;

    ObjectMapper mapper = new ObjectMapper();

    @DynamicPropertySource
    static void wireMockProperties(DynamicPropertyRegistry registry) {
        registry.add("storehub.catalog-base-url",
                () -> "http://localhost:" + WIREMOCK_PORT);
        registry.add("storehub.payment-base-url",
                () -> "http://localhost:" + WIREMOCK_PORT);
        registry.add("spring.security.oauth2.client.provider.storehub-keycloak.token-uri",() ->
                "http://localhost:"+WIREMOCK_PORT+ "/realms/myrealm/protocol/openid-connect/token");
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withInitScript("init.sql");
    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void cleanDb() {
        orderRepository.deleteAll().block();
        cartRepository.deleteAll().block();
    }


    @Test
    void placeOrder_createsOrderAndReturnsResponse_onHappyPath() throws JsonProcessingException {
        UUID storeId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofGuest(UUID.randomUUID());
        UUID cartId = seedCart(storeId, owner);
        List<UUID> inventoryReservationIds = List.of(UUID.randomUUID());
        UUID slotRId = UUID.randomUUID();

        stubForAvailable(storeId, cartId, slotId);
        stubPricingSuccess();

        UUID expectedPaymentId = UUID.randomUUID();
        String expectedApprovalUrl = "https://www.sandbox.paypal" +
                ".com/checkoutnow?token=3YE25194U0140084J";
        stubPaymentSuccess(expectedPaymentId, expectedApprovalUrl);

        stubForReservation(inventoryReservationIds, slotRId);

        stubKeycloak();



        OrderRequest orderRequest = new OrderRequest(storeId, cartId, slotId, "buyer@example.com",
                billingAddress(), deliveryAddress(), "abdlekhalek", "saadani", "23725059");
        UUID idempotencyKey = UUID.randomUUID();

        webTestClient.post().uri("/api/orders")
                .header("Idempotency-Key", idempotencyKey.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderCreatedResponse.class)
                .value(response -> {
                    log.debug("the response is {}", response);
                    assertThat(response.orderId()).isNotNull();
                    assertThat(response.paymentId()).isEqualTo(expectedPaymentId);
                    assertThat(response.paymentApprovalUrl()).isEqualTo(expectedApprovalUrl);
                });

        // real DB assertion — proves the pipeline actually persisted correctly
        List<Order> orders = orderRepository.findAll().collectList().block();
        assertThat(orders).hasSize(1);
        assertThat(orders.get(0).getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(orders.get(0).getIdempotencyKey()).isEqualTo(idempotencyKey);
        assertThat(orders.get(0).getInventoryRetainIds()).isEqualTo(inventoryReservationIds);
        assertThat(orders.get(0).getSlotRetainId()).isEqualTo(slotRId);
    }

    @Test
    void placeOrder_setsGuestHeader_whenGuestCheckout()  throws JsonProcessingException {
        UUID storeId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofGuest(UUID.randomUUID());
        UUID cartId = seedCart(storeId, owner);

        stubForAvailable(storeId, cartId, slotId);
        stubPricingSuccess();
        stubPaymentSuccess();
        stubForReservation();


        OrderRequest orderRequest = new OrderRequest(storeId, cartId, slotId, "guest@example.com",
                billingAddress(), deliveryAddress(), null, null, "+21623725059");

        webTestClient.post().uri("/api/orders")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                // no X-Guest-Id sent -> OwnerResolver.resolveGuest generates one
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists("X-Guest-Id");
    }

    @Test
    void placeOrder_returnsExistingOrder_noNewRowCreated_whenIdempotencyKeyReused() throws JsonProcessingException {
        UUID storeId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofGuest(UUID.randomUUID());
        UUID cartId = seedCart(storeId, owner);
        UUID idempotencyKey = UUID.randomUUID();

        stubForAvailable(storeId, cartId, slotId);
        stubPricingSuccess();
        stubPaymentSuccess();
        stubForReservation();

        OrderRequest orderRequest = new OrderRequest(storeId, cartId, slotId, "buyer@example.com",
                billingAddress(), deliveryAddress(), null, null, null);

        // first call creates the order
        OrderCreatedResponse ocr = webTestClient.post().uri("/api/orders")
                .header("Idempotency-Key", idempotencyKey.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderRequest)

                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderCreatedResponse.class)
                .returnResult()
                .getResponseBody();

        // second call, same idempotency key -> should short-circuit, not create a new row
        webTestClient.post().uri("/api/orders")
                .header("Idempotency-Key", idempotencyKey.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderCreatedResponse.class)
                .value(response -> {
                    assertThat(response.orderId()).isEqualTo(ocr.orderId());
                });

        List<Order> orders = orderRepository.findByIdempotencyKey(idempotencyKey)
                .flux().collectList().block();
        assertThat(orders).hasSize(1);
        assertThat(orders.getFirst().getId()).isEqualTo(ocr.orderId());
    }

    @Test
    void placeOrder_returns4xxAndCreatesNoOrder_whenSlotUnavailable() {
        UUID storeId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        CartOwner owner = CartOwner.ofGuest(UUID.randomUUID());
        UUID cartId = seedCart(storeId, owner);

        stubInventoryAvailable(true);
        stubSlotAvailable(false);

        OrderRequest orderRequest = new OrderRequest(storeId, cartId, slotId, "buyer@example.com",
                billingAddress(), deliveryAddress(), null, null, null);

        webTestClient.post().uri("/api/orders")
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus()
                .is4xxClientError(); // adjust to actual mapped status for UnavailableException

        assertThat(orderRepository.count().block()).isZero();
    }


    // ---- helpers ----
    private UUID seedCart(UUID storeId, CartOwner owner) {
        CartEntity cart = new CartEntity();
        if (owner.isGuest()) cart.setGuestId(owner.guestId());
        else cart.setUserId(owner.userId());
        cart.setStoreId(storeId);
        CartItemEntity item = new CartItemEntity(UUID.randomUUID(), 2);
        item.setUnitPrice(BigDecimal.TEN);
        item.setOriginalLineTotal(BigDecimal.valueOf(20));
        item.setFinalLineTotal(BigDecimal.valueOf(20));
        item.setDiscountAmount(BigDecimal.ZERO);
        cart.setItems(List.of(item));
        cart.setOriginalTotal(BigDecimal.valueOf(20));
        cart.setFinalTotal(BigDecimal.valueOf(20));
        cart.setTotalDiscount(BigDecimal.ZERO);
        return cartRepository.save(cart).block().getId();
    }

    private void stubKeycloak() {
        stubFor(post(urlEqualTo("/realms/myrealm/protocol/openid-connect/token"))
                .willReturn(okJson("""
        {
          "access_token": "fake-jwt-token",
          "token_type": "Bearer",
          "expires_in": 300
        }
        """)));
    }

    private void stubForReservation() throws JsonProcessingException {
        stubForReservation(List.of(UUID.randomUUID()), UUID.randomUUID());
    }

    private void stubForReservation(List<UUID> inventoryReservationIds, UUID slotReservationId) throws JsonProcessingException {

        stubInventoryReservation(inventoryReservationIds);
        stubSlotReservation(slotReservationId);

    }

    private void stubInventoryReservation(List<UUID> reservationIds) throws JsonProcessingException {
        stubFor(post(urlPathEqualTo("/api/inventory/reservations"))
                .willReturn(okJson("{\"retainIds\": " + mapper.writeValueAsString(reservationIds) + "}")));
    }

    private void stubSlotReservation(UUID reservationId) throws JsonProcessingException {
        stubFor(post(urlPathEqualTo("/api/delivery-slots/reserve"))
                .willReturn(okJson("{\"retainId\": " + mapper.writeValueAsString(reservationId) + "}")));
    }

    private void stubForAvailable(UUID storeId, UUID cartId, UUID slotId) {
        stubInventoryAvailable(true);
        stubSlotAvailable(true);
    }

    private void stubInventoryAvailable(boolean available) {
        stubFor(post(urlPathEqualTo("/api/inventory/check-availability"))
                .willReturn(okJson("{\"available\": " + available + "}")));
    }

    private void stubSlotAvailable(boolean available) {
        stubFor(get(urlPathEqualTo("/api/delivery-slots/check-availability"))
                .willReturn(okJson("{\"available\": " + available + "}")));
    }

    private void stubPricingSuccess() {
        stubFor(post(urlPathEqualTo("/internal/prices"))
                .willReturn(okJson("""
                        {"items": [], "originalTotal": 20, "finalTotal": 20, "totalDiscount": 0}
                        """)));
    }

    private void stubPaymentSuccess() {
        stubPaymentSuccess(UUID.randomUUID(), "randomstring");
    }
    private void stubPaymentSuccess(UUID id, String approvalUrl) {
        stubFor(post(urlPathEqualTo("/api/payments/paypal"))
                .willReturn(okJson(
                        """
                                {
                                "paymentId": "%s",
                                "paymentOrderId": "9EE99600LX3657029",
                                 "status": "CREATED",
                                 "approvalUrl": "%s",
                                 "message": "Payment created successfully"
                                 }
                                """.formatted(id, approvalUrl)
                )));
    }


    private String billingAddress() {
        return "The billing, address";
    }

    private String deliveryAddress() {
        return "The delivery, address";
    }
}
