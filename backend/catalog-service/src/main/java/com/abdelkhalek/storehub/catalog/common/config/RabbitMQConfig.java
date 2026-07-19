package com.abdelkhalek.storehub.catalog.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {

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
        return factory;
    }

    @Bean
    TopicExchange storehubExchange() {
        return new TopicExchange("storehub.exchange", true, false);
    }

    @Bean
    Queue storeCreatedQueue() {
        return new Queue("store.created.queue");
    }

    @Bean
    Binding binding(Queue storeCreatedQueue, TopicExchange storehubExchange) {
        return BindingBuilder.bind(storeCreatedQueue).to(storehubExchange).with("store.created");
    }

    @Bean
    Queue userCreatedQueue() {
        return new Queue("user.created.queue");
    }

    @Bean
    Binding userBinding(Queue userCreatedQueue, TopicExchange storehubExchange) {
        return BindingBuilder.bind(userCreatedQueue).to(storehubExchange).with("user.created");
    }


}
