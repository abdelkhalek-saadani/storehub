package proxiad.oms.pricing.infrastructure.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import proxiad.oms.pricing.domain.models.DomainEvent;
import proxiad.oms.pricing.domain.models.TotalCalculatedEvent;
import proxiad.oms.pricing.domain.spi.EventPublisher;

import java.util.concurrent.CompletableFuture;

@Component
public class KafkaEventPublisher implements EventPublisher {



        @Autowired
        private KafkaTemplate<String, TotalCalculatedEvent> totalCalculatedKafkaTemplate;


        @Value(value = "${total.topic.name}")
        private String totalCalculatedTopicName;



    @Override
    public void publish(DomainEvent event) {
        if ((event instanceof TotalCalculatedEvent totalCalculatedEvent)) {
            sendTotalCalculatedEvent(totalCalculatedEvent);}
        else {
            throw new UnsupportedOperationException("Unsupported event type: " + event.getClass().getName());
        }
    }


        private void sendTotalCalculatedEvent(TotalCalculatedEvent event) {
            System.out.println("Sending TotalCalculatedEvent message=[" + event + "]");
            CompletableFuture<SendResult<String,TotalCalculatedEvent>> future =  totalCalculatedKafkaTemplate.send(totalCalculatedTopicName, event);
            future.whenComplete((result, ex) -> {

                if (ex == null) {
                    System.out.println("Sent message=[" + event + "] with offset=[" + result.getRecordMetadata()
                            .offset() + "]");
                } else {
                    System.out.println("Unable to send message=[" + event + "] due to : " + ex.getMessage());
                }
            });

        }
    }


