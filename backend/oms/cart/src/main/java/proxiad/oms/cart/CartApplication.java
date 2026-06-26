package proxiad.oms.cart;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import proxiad.oms.cart.domain.CartService;
import proxiad.oms.cart.domain.models.Cart;
import proxiad.oms.cart.domain.models.CartItem;
import proxiad.oms.cart.domain.models.ItemAddedEvent;
import proxiad.oms.cart.domain.models.Money;
import proxiad.oms.cart.domain.spi.EventPublisher;
import com.proxiad.events.CartChangedEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
@SpringBootApplication
@Slf4j
public class CartApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(CartApplication.class, args);

		CartService cartService = context.getBean(CartService.class);

		EventPublisher eventPublisher = context.getBean(EventPublisher.class);


		String productId1 = "53af7942-0b98-4b0e-a585-3274aca7614a";
		String productId2 = "63d363df-2db5-430d-a142-0dfe600283d6";
		String productId3 = "bca25930-ffd4-44c0-b730-6dd487c4ef2c";
		// Nkamel naemel cart CHANGED EVENT BCH NTESTI BEHA EL PRICIGN, baed njareb kafka listener return Mono void
		CartItem item1 = new CartItem(UUID.fromString(productId1),2);
		CartItem item2 = new CartItem(UUID.fromString(productId2),11);
		CartItem item3 = new CartItem(UUID.fromString(productId3),1);

		List<CartItem> items = new ArrayList<>();
		items.add(item1);
		items.add(item2);
		items.add(item3);


		ItemAddedEvent itemAddedEvent = new ItemAddedEvent(UUID.fromString("c658fa8b-dbcc-48a4-a80a-1e2d547de79a"),items);


		eventPublisher.publish(itemAddedEvent);

		// Create
//		UUID customerId = UUID.randomUUID();
//		Cart cart = new Cart(customerId);
//		log.info("Cart Total {}", cart.getTotal());
//		Cart savedCart = cartService.create(cart);
//		UUID cartId = savedCart.getId();
//		log.info("Cart ID: {}", cartId);
//		log.info("Saved cart {}", savedCart);

		// Set Quantity
//		UUID productId1 = UUID.randomUUID();
//		UUID productId2 = UUID.randomUUID();
//		cartService.setQuantity(cartId,productId1,1);
//		cartService.setQuantity(cartId,productId2,10);
//		Cart quantityCart = cartService.getCart(cartId);
//		log.info("Quantity update completed.");
//		log.info("Cart after update: {}", quantityCart);

		// Set Total
//		Money total = new Money(BigDecimal.valueOf(999));
//		Cart totalCart = cartService.setTotal(cartId,total);
//		log.info("Total update completed.");
//		log.info("Cart after update: {}", totalCart);

		// Reset
//		cartService.reset(cartId);
//		Cart resetCart = cartService.getCart(cartId);
//		log.info("Cart after Reset: {}", resetCart);

		// Set Quantity Again
//		cartService.setQuantity(cartId,productId1,2);
//		cartService.setQuantity(cartId,productId2,20);
//		Cart quantityAgainCart = cartService.getCart(cartId);
//		log.info("Quantity update again completed.");
//		log.info("Cart after update: {}", quantityAgainCart);

		// Delete
//		cartService.delete(cartId);
//		try {
//			log.info("Attempt to get deleted cart {}", cartId);
//			Cart deletedCart = cartService.getCart(cartId);
//			log.info("This is weird, we got a deleted cart {}", cartId);
//		} catch (Exception e) {
//			log.info("Cart not found with ID: {}", cartId);
//			log.info("everything is cool {}" , e.getMessage());
//		}



	}

}
