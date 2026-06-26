package proxiad.oms.cart.infrastructure.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import proxiad.oms.cart.domain.CartService;
import proxiad.oms.cart.domain.models.Money;
import proxiad.oms.cart.domain.spi.EventListener;
import proxiad.oms.cart.infrastructure.mappers.MoneyEventMapper;
import proxiad.oms.cart.infrastructure.models.TotalCalculatedEvent;

import java.util.UUID;


@Component
public class KafkaEventListener implements EventListener {

    @Autowired
    private CartService cartService;

    @Autowired
    private MoneyEventMapper moneyEventMapper;


    @KafkaListener(topics = "${total.topic.name}", containerFactory = "totalCalculatedKafkaListenerContainerFactory")
    public void totalListener(TotalCalculatedEvent event) {
        System.out.println("Received totalCalculatedEvent message: " + event);

        try {
            Money total = moneyEventMapper.fromMoneyEvent(event.getTotal());
            // here we will convert the received id string -> UUID
            UUID cartUUID = UUID.fromString(event.getCartId());
            cartService.setTotal(cartUUID, total);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}