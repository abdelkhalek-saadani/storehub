package com.abdelkhalek.storehub.catalog.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(StorehubProperties.class)

public class RabbitMQConfig {

    private final StorehubProperties props;

    private static final List<QueueDef> QUEUE_DEFS = List.of(
            new QueueDef("store.created.queue", "store.created"),
            new QueueDef("user.created.queue", "user.created"),
            new QueueDef("items.released.queue", "items.released"),
            new QueueDef("slot.released.queue", "slot.released"),
            new QueueDef("inventory.order.created.queue", "order.created"),
            new QueueDef("slot.order.created.queue", "order.created")
    );

    private record QueueDef(String queueName, String routingKey) {}

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    Declarables topology() {
        String exchangeName = props.rabbit().exchange();
        String dlxName = exchangeName + ".dlx";

        TopicExchange exchange = new TopicExchange(exchangeName, true, false);
        TopicExchange dlx = new TopicExchange(dlxName, true, false);

        List<Declarable> declarables = new ArrayList<>(List.of(exchange, dlx));

        for (QueueDef def : QUEUE_DEFS) {
            Queue queue = QueueBuilder.durable(def.queueName())
                    .withArgument("x-dead-letter-exchange", dlxName)
                    .withArgument("x-dead-letter-routing-key", def.routingKey())
                    .build();

            String dlqName = def.queueName().replace(".queue", ".dlq");
            Queue dlq = QueueBuilder.durable(dlqName).build();

            declarables.add(queue);
            declarables.add(BindingBuilder.bind(queue).to(exchange).with(def.routingKey()));
            declarables.add(dlq);
            declarables.add(BindingBuilder.bind(dlq).to(dlx).with(def.routingKey()));
        }

        return new Declarables(declarables);
    }



}
