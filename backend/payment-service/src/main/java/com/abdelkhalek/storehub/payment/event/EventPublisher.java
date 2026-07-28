package com.proxiad.payment.event;

import com.proxiad.payment.common.config.StorehubProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableConfigurationProperties(StorehubProperties.class)
@RequiredArgsConstructor
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final StorehubProperties props;


    public void publish(PaymentStatusUpdateEvent event){
        log.debug("Publishing {} event: {}",event.getClass().getSimpleName(), event);
        rabbitTemplate.convertAndSend(props.rabbit().exchange(), "payment.status.updated", event);
    };

}
