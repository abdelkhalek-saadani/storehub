package com.abdelkhalek.storehub.order.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

@Configuration
@Slf4j
@EnableConfigurationProperties(StorehubProperties.class)
@RequiredArgsConstructor
public class RabbitConfig {


    @Bean
    Mono<Connection> rabbitConnectionMono(RabbitConnectionDetails details) {
        ConnectionFactory factory = new ConnectionFactory();
        RabbitConnectionDetails.Address addr = details.getFirstAddress();
        factory.setHost(addr.host());
        factory.setPort(addr.port());
        factory.setUsername(details.getUsername());
        factory.setPassword(details.getPassword());
        factory.useNio();
        return Mono.fromCallable(factory::newConnection).cache();
    }


    @Bean
    Sender sender(Mono<Connection> connectionMono) {
        return RabbitFlux.createSender(new SenderOptions().connectionMono(connectionMono));
    }

    @Bean
    ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.build();
    }

    @Bean
    Receiver receiver(Mono<Connection> connectionMono) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connectionMono));
    }


}