package com.abdelkhalek.storehub.order.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.RabbitFlux;
import reactor.rabbitmq.Sender;
import reactor.rabbitmq.SenderOptions;

@Configuration
@Slf4j
public class RabbitConfig {


    @Bean
    Mono<Connection> rabbitConnectionMono(
            @Value("${spring.rabbitmq.host}") String host,
            @Value("${spring.rabbitmq.port}") int port,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password) {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        factory.useNio();

        return Mono.fromCallable(factory::newConnection).cache();
    }


    @Bean
    Sender sender(Mono<Connection> connectionMono) {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
    }

    @Bean
    ApplicationRunner declareExchange(Sender sender) {
        return args -> sender.declareExchange(ExchangeSpecification.exchange("storehub.exchange")
                .type("topic").durable(true)).block();
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }
}