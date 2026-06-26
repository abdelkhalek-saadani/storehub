package proxiad.oms.pricing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import proxiad.oms.pricing.application.implementations.KafkaEventListener;
import proxiad.oms.pricing.application.mappers.CartChangedEventMapper;
//import com.proxiad.events.CartChangedEvent;
import com.proxiad.events.CartChangedEvent;
import proxiad.oms.pricing.domain.DiscountService;
import proxiad.oms.pricing.domain.models.*;
import proxiad.oms.pricing.domain.spi.EventPublisher;
import proxiad.oms.pricing.domain.strategies.DiscountStrategy;
import proxiad.oms.pricing.domain.factories.DiscountStrategyFactory;
import proxiad.oms.pricing.infrastructure.implementations.KafkaEventPublisher;


//import com.proxiad.events.CartItemEvent;
import com.proxiad.events.CartItemEvent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class PricingApplication {

	@Autowired
	private CartChangedEventMapper cartChangedEventMapper;

	public static void main(String[] args) throws Exception{

		ConfigurableApplicationContext context =  SpringApplication.run(PricingApplication.class, args);

		// because cant inject it here :/
		CartChangedEventMapper cartChangedEventMapper = context.getBean(CartChangedEventMapper.class);
		DiscountService discountService = context.getBean(DiscountService.class);

//		EventPublisher producer = context.getBean(EventPublisher.class);
//
//		Money total = new Money(BigDecimal.valueOf(66666));
//		TotalCalculatedEvent totalCalculatedEvent = new TotalCalculatedEvent("c658fa8b-dbcc-48a4-a80a-1e2d547de79a",total);
//		producer.publish(totalCalculatedEvent);


		// TEST start
		//get cart
		/* I tested this combination of items(productId,qty) from the swagger endpoint, it passed*/
		String productId1 = "53af7942-0b98-4b0e-a585-3274aca7614a";
		String productId2 = "63d363df-2db5-430d-a142-0dfe600283d6";
		String productId3 = "bca25930-ffd4-44c0-b730-6dd487c4ef2c";

		CartItemEvent item1 = new CartItemEvent(productId1,2);
		CartItemEvent item2 = new CartItemEvent(productId2,11);
		CartItemEvent item3 = new CartItemEvent(productId3,1);
		List<CartItemEvent> items = new ArrayList<>();
		items.add(item1);
		items.add(item2);
		items.add(item3);

		CartChangedEvent cartChangedEvent = new CartChangedEvent();
		cartChangedEvent.setCartItems(items);
		cartChangedEvent.setCartId("c658fa8b-dbcc-48a4-a80a-1e2d547de79a");

		log.info("The event CartChangedEvent : {}", cartChangedEvent);

		Cart cart = cartChangedEventMapper.toCart(cartChangedEvent);


		log.info("Shopping Cart before: {}", cart);


		//think about using subscribeOn
		// .subscribeOn(Schedulers.boundedElastic())
		discountService.calculateTotal(cart).subscribe();

		log.info("nothing blocking");


		//get discounts
//		List<Discount> discounts = new ArrayList<>();
//
//		Map<String, String> discount1_attributes= new HashMap<String,String>();
//		discount1_attributes.put("percentage","30");
//		Discount discount1 = new Discount("PERCENTAGE","productId1", discount1_attributes);
//		discounts.add(discount1);
//
//		Map<String, String> discount2_attributes= new HashMap<String,String>();
//		discount2_attributes.put("percentage","30");
//		discount2_attributes.put("minimumQuantity","10");
//		Discount discount2 = new Discount("QUANTITY","productId2", discount2_attributes);
//		discounts.add(discount2);
//
//		//Transform them into list of strategies
//
//		for (Discount discount: discounts) {
//			log.info("discount: {}", discount);
//		}
//
//
//		for (Discount discount: discounts) {
//			DiscountStrategy discountStrategy = DiscountStrategyFactory.getDiscountStrategy(discount);
//			log.info("Applying {}" , discountStrategy);
//			discountStrategy.apply(cart);
//		}

		log.info("Shopping Cart after: {}", cart);





	}













}
