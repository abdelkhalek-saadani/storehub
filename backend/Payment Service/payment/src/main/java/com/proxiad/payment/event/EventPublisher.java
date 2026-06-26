package com.proxiad.payment.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EventPublisher {

    public void publish(PaymentStatusEvent event){
        log.info("DomainEvent: " + event);
        log.info("publishing event ...");
    };

}
