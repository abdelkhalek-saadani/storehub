package proxiad.oms.pricing.application.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import proxiad.oms.pricing.application.mappers.CartChangedEventMapper;
import com.proxiad.events.CartChangedEvent;
import proxiad.oms.pricing.domain.DiscountService;
import proxiad.oms.pricing.domain.models.Cart;
import proxiad.oms.pricing.domain.spi.EventListener;

@Slf4j
@Component
public class KafkaEventListener implements EventListener {

        @Autowired
        private DiscountService discountService;

        @Autowired
        private CartChangedEventMapper cartChangedEventMapper;

        @KafkaListener(topics = "${cart.topic.name}", containerFactory = "cartChangedKafkaListenerContainerFactory")
        // is it possible to make a kafka listener with mono<void> return type ?
        public void cartChangedListener(CartChangedEvent event) {
            log.info("Received cartChangedEvent message: {}" , event);
            Cart cart = cartChangedEventMapper.toCart(event);
            log.info("Cart received in the listener: {}", cart);
            // TODO: nomenclature à voir
            discountService.calculateTotal(cart).subscribe();

        }
    }


