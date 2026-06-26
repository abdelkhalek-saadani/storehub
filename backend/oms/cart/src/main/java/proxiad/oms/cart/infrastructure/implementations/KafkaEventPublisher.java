package proxiad.oms.cart.infrastructure.implementations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import proxiad.oms.cart.domain.models.ItemQuantityChangedEvent;
import proxiad.oms.cart.infrastructure.mappers.CartChangedEventMapper;
import com.proxiad.events.CartChangedEvent;
import proxiad.oms.cart.domain.models.DomainEvent;
import proxiad.oms.cart.domain.models.ItemAddedEvent;
import proxiad.oms.cart.domain.models.ItemRemovedEvent;
import proxiad.oms.cart.domain.spi.EventPublisher;
import org.springframework.kafka.support.SendResult;


import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaEventPublisher implements EventPublisher {
    @Autowired
    private KafkaTemplate<String, CartChangedEvent> cartChangedKafkaTemplate;

    // Add other kafka templates for different event types

    @Value("${cart.topic.name}")
    private String cartChangedTopicName;

    @Autowired
    private CartChangedEventMapper cartChangedEventMapper;

    @Override
    public void publish(DomainEvent event) {
        switch (event) {
            case ItemAddedEvent itemAddedEvent ->
                    sendCartChangedEvent(cartChangedEventMapper.fromItemAddedEvent(itemAddedEvent));
            case ItemRemovedEvent itemRemovedEvent ->
                    sendCartChangedEvent(cartChangedEventMapper.fromItemRemovedEvent(itemRemovedEvent));
            case ItemQuantityChangedEvent itemQuantityChangedEvent ->
                    sendCartChangedEvent(cartChangedEventMapper.fromItemQuantityChangedEvent(itemQuantityChangedEvent));
            case null, default ->
                    throw new UnsupportedOperationException("Unsupported event type: " + event.getClass().getName());
        }
    }

    private void sendCartChangedEvent(CartChangedEvent event) {
        log.info("Sending CartChangedEvent message=[ {} ]", event );
        CompletableFuture<SendResult<String, CartChangedEvent>> future =
                cartChangedKafkaTemplate.send(cartChangedTopicName, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[ {} ] with offset=[ {} ]",event,
                        result.getRecordMetadata().offset());
            } else {
                log.info("Unable to send message=[ {} ] due to : {}", event,ex.getMessage()  );
            }
        });
    }
}
