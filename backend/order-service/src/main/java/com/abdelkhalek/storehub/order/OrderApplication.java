package com.abdelkhalek.storehub.order;

import com.abdelkhalek.storehub.order.order.OrderService;
import com.abdelkhalek.storehub.order.order.models.*;
import com.abdelkhalek.storehub.order.order.repository.OrderReactiveRepository;
import com.abdelkhalek.storehub.order.order.entity.OrderEntity;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@OpenAPIDefinition(
		info = @Info(
				title = "OpenAPI definition",
				version = "1.0.0"
		),
		servers = {
				@Server(url = "http://localhost:8081", description = "Local Development"),
				@Server(url = "https://api.yourcompany.com", description = "Production")
		}
)
@Slf4j
@SpringBootApplication
public class OrderApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(OrderApplication.class, args);

		OrderService orderService = ctx.getBean(OrderService.class);

		OrderReactiveRepository orderReactiveRepository = ctx.getBean(OrderReactiveRepository.class);
		// Arrange
		Cart cart = new Cart();
		/*OrderItem item1 = new OrderItem(
				UUID.randomUUID(),
				5,
				BigDecimal.valueOf(50),
				BigDecimal.TEN,
				BigDecimal.TEN);
		OrderItem item2 = new OrderItem(
				UUID.randomUUID(),
				10,
				new Money(BigDecimal.valueOf(20)),
				new Money(BigDecimal.TWO),
				new Money(BigDecimal.TWO));
		cart.setItems(List.of(item1, item2));*/

		Delivery delivery = new Delivery();
		Address address = new Address();
		address.setCity("city2");
		delivery.setAddress(address);
		delivery.setMode(DeliveryMode.PICKUP);

		Slot slot = Slot.getDefaultSlot();

		Coupon coupon = new Coupon("some-code");

		Store store = new Store(UUID.fromString("8a9b0427-8cc9-4c79-8410-0d0127bafd72"));

		// Act & Assert
		Order order = orderService.placeOrderWithCashPayment(cart,delivery,slot,coupon,store)
				.block();
		log.info("Order processed successfully");

		PaymentLink paymentLink = orderService.placeOrderWithOnlinePayment(cart,delivery,slot,coupon,store)
				.block();
		log.info("Order processed successfully1");

		OrderEntity order2 = orderReactiveRepository.findById(order.getId()).block();

	}

}
