package com.abdelkhalek.storehub.order.implementations;

import com.abdelkhalek.storehub.order.order.models.DeliveryMode;
import com.abdelkhalek.storehub.order.order.models.PaymentMode;
import com.abdelkhalek.storehub.order.order.repository.OrderReactiveRepository;
import com.abdelkhalek.storehub.order.infrastructure.models.order.AddressEntity;
import com.abdelkhalek.storehub.order.infrastructure.models.order.MoneyEntity;
import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import com.abdelkhalek.storehub.order.infrastructure.models.order.SlotEntity;
import io.github.joselion.springr2dbcrelationships.R2dbcRelationshipsCallbacks;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
@Testcontainers
@TestPropertySource(properties = "spring.flyway.enabled=false")
@Import({OrderRepositoryTest.RelationshipsTestConfig.class})
class OrderRepositoryTest {

    @TestConfiguration
    static class RelationshipsTestConfig {

        @Bean
        public <T> R2dbcRelationshipsCallbacks<T> relationshipsCallbacks(
                @Lazy R2dbcEntityTemplate template,
                ApplicationContext context
        ) {
            return new R2dbcRelationshipsCallbacks<>(template, context);
        }
    }

    private OrderEntity orderEntity;

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private OrderReactiveRepository orderReactiveRepository;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        String jdbcUrl = postgres.getJdbcUrl();
        Flyway flyway = Flyway.configure()
                .dataSource(jdbcUrl, postgres.getUsername(), postgres.getPassword())
                .locations("classpath:db/postgres/test-migration")
                .load();

        flyway.migrate();
    }

    /*@Test
    void shouldSaveAndFindOrder() {
        orderEntity = createOrderEntity();

        StepVerifier.create(orderReactiveRepository.save(orderEntity)
                        .flatMap(saved -> orderReactiveRepository.findById(saved.getId()))
                )
                .expectNextMatches(found -> {
                    assertThat(found.getCartItems()).hasSize(orderEntity.getCartItems().size());
                    assertThat(found.getDeliveryMode()).isEqualTo(orderEntity.getDeliveryMode());
                    assertThat(found.getPaymentMode()).isEqualTo(orderEntity.getPaymentMode());

                    // Verify cart items are properly saved and loaded
                    UUID firstProductId = orderEntity.getCartItems().getFirst().getProductId();
                    UUID lastProductId = orderEntity.getCartItems().getLast().getProductId();

                    boolean hasFirstProduct = found.getCartItems().stream()
                            .anyMatch(item -> item.getProductId().equals(firstProductId));
                    boolean hasLastProduct = found.getCartItems().stream()
                            .anyMatch(item -> item.getProductId().equals(lastProductId));

                    return hasFirstProduct && hasLastProduct;
                })
                .verifyComplete();
    }*/

    @Test
    void shouldReturnEmptyWhenOrderNotFound() {
        UUID nonExistentId = UUID.randomUUID();

        StepVerifier.create(orderReactiveRepository.findById(nonExistentId))
                .verifyComplete();
    }

    /*@Test
    void shouldDeleteOrder() {
        orderEntity = createOrderEntity();

        StepVerifier.create(orderReactiveRepository.save(orderEntity)
                        .flatMap(saved -> orderReactiveRepository.deleteById(saved.getId())
                                .then(orderReactiveRepository.findById(saved.getId()))))
                .verifyComplete();
    }*/

    /*@Test
    void shouldFindAllOrders() {
        OrderEntity order1 = createOrderEntity();
        OrderEntity order2 = createOrderEntity();

        StepVerifier.create(orderReactiveRepository.save(order1)
                        .then(orderReactiveRepository.save(order2))
                        .thenMany(orderReactiveRepository.findAll()))
                .expectNextCount(2)
                .verifyComplete();
    }*/

    @Test
    void shouldSaveOrderWithEmptyCartItems() {
        OrderEntity orderWithoutItems = createOrderEntityWithoutCartItems();

        StepVerifier.create(orderReactiveRepository.save(orderWithoutItems)
                        .flatMap(saved -> orderReactiveRepository.findById(saved.getId())))
                .expectNextMatches(found -> {
                    assertThat(found.getCartItems()).isEmpty();
                    assertThat(found.getDeliveryMode()).isEqualTo(orderWithoutItems.getDeliveryMode());
                    return true;
                })
                .verifyComplete();
    }

    /*@Test
    void shouldUpdateOrder() {
        orderEntity = createOrderEntity();

        StepVerifier.create(orderReactiveRepository.save(orderEntity)
                        .flatMap(saved -> {
                            saved.setPaymentMode(PaymentMode.CASH);
                            return orderReactiveRepository.save(saved);
                        })
                        .flatMap(updated -> orderReactiveRepository.findById(updated.getId())))
                .expectNextMatches(found -> found.getPaymentMode() == PaymentMode.CASH)
                .verifyComplete();
    }*/

    private OrderEntity createOrderEntityWithoutCartItems() {
        LocalDateTime date = LocalDateTime.now();
        AddressEntity deliveryAddress = new AddressEntity(null, "Delivery City", "DC", 12345);
        AddressEntity invoiceAddress = new AddressEntity(null, "456 Invoice Rd", "Invoice City", 12345);
        DeliveryMode deliveryMode = DeliveryMode.PICKUP;

        SlotEntity slot = new SlotEntity();
        slot.setDate(LocalDate.now());
        slot.setStartTime(LocalTime.now());
        slot.setEndTime(LocalTime.now().plusHours(1));

        PaymentMode paymentMode = PaymentMode.CASH;
        MoneyEntity originalSubtotal = new MoneyEntity(null, BigDecimal.TEN, "EUR");
        MoneyEntity subtotal = new MoneyEntity(null, BigDecimal.TEN, "EUR");
        MoneyEntity deliveryFee = new MoneyEntity(null, BigDecimal.TEN, "EUR");
        MoneyEntity total = new MoneyEntity(null, BigDecimal.TEN, "EUR");

        UUID slotRetainId = UUID.randomUUID();
        UUID inventoryRetainId = UUID.randomUUID();

        return new OrderEntity(
                null, date, deliveryAddress.getId(), deliveryAddress, invoiceAddress, invoiceAddress.getId(),
                deliveryMode, slot, slot.getId(), paymentMode, originalSubtotal, originalSubtotal.getId(),
                subtotal, subtotal.getId(), deliveryFee, deliveryFee.getId(), total, total.getId(),
                new ArrayList<>(), slotRetainId, inventoryRetainId
        );
    }

    /*public static OrderEntity createOrderEntity() {
        LocalDateTime date = LocalDateTime.now();

        AddressEntity deliveryAddress = new AddressEntity(null, "Delivery City", "DC", 12345);
        AddressEntity invoiceAddress = new AddressEntity(null, "456 Invoice Rd", "Invoice City", 12345);
        DeliveryMode deliveryMode = DeliveryMode.PICKUP;

        SlotEntity slot = new SlotEntity();
        slot.setDate(LocalDate.now());
        slot.setStartTime(LocalTime.now());
        slot.setEndTime(LocalTime.now().plusHours(1));

        PaymentMode paymentMode = PaymentMode.CASH;
        MoneyEntity originalSubtotal = new MoneyEntity(null, BigDecimal.TEN, "EUR");
        MoneyEntity subtotal = new MoneyEntity(null, BigDecimal.TEN, "EUR");
        MoneyEntity deliveryFee = new MoneyEntity(null, BigDecimal.TEN, "EUR");
        MoneyEntity total = new MoneyEntity(null, BigDecimal.TEN, "EUR");

        List<CartItemEntity> cartItems = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            UUID productId = UUID.randomUUID();
            UUID moneyId1 = UUID.randomUUID();
            UUID moneyId2 = UUID.randomUUID();
            UUID moneyId3 = UUID.randomUUID();
            CartItemEntity item = new CartItemEntity(
                    null, productId, (i + 1),
                    new MoneyEntity(null, BigDecimal.TEN, "EUR"), moneyId1,
                    new MoneyEntity(null, BigDecimal.TEN, "EUR"), moneyId2,
                    new MoneyEntity(null, BigDecimal.TEN, "EUR"), moneyId3,
                    null, null);
            cartItems.add(item);
        }

        UUID slotRetainId = UUID.randomUUID();
        UUID inventoryRetainId = UUID.randomUUID();

        OrderEntity order = new OrderEntity(
                null, date, deliveryAddress.getId(), deliveryAddress, invoiceAddress, invoiceAddress.getId(),
                deliveryMode, slot, slot.getId(), paymentMode, originalSubtotal, originalSubtotal.getId(),
                subtotal, subtotal.getId(), deliveryFee, deliveryFee.getId(), total, total.getId(),
                cartItems, slotRetainId, inventoryRetainId
        );

        order.setSlot(slot);
        return order;
    }*/
}