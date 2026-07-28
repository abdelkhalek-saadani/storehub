package com.abdelkhalek.storehub.order.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;

import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
@EnableConfigurationProperties(RabbitProperties.class)
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
    ObjectMapper objectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Bean
    Receiver receiver(Mono<Connection> connectionMono) {
        return RabbitFlux.createReceiver(new ReceiverOptions().connectionMono(connectionMono));
    }

    @Bean
    ApplicationRunner topology(Sender sender, RabbitProperties props) {
        record Binding(String queue, String routingKey) {
        }

        String exchangeName = props.exchange();
        String dlxName = exchangeName + ".dlx";

        List<Binding> bindings = List.of(
                new Binding("payment.status.queue", "payment.status.updated")
        );

        return args -> sender.declareExchange(ExchangeSpecification.exchange(exchangeName)
                        .type("topic").durable(true))
                .then(sender.declareExchange(ExchangeSpecification.exchange(dlxName)
                        .type("topic").durable(true)))
                .thenMany(Flux.fromIterable(bindings)
                        .flatMap(b -> {
                            String dlqName = b.queue().replace(".queue", ".dlq");

                            return sender.declareQueue(QueueSpecification.queue(b.queue())
                                            .durable(true)
                                            .arguments(Map.of(
                                                    "x-dead-letter-exchange", dlxName,
                                                    "x-dead-letter-routing-key", b.routingKey()
                                            )))
                                    .then(sender.bind(BindingSpecification.binding(
                                            exchangeName, b.routingKey(), b.queue())))
                                    .then(sender.declareQueue(QueueSpecification.queue(dlqName)
                                            .durable(true)))
                                    .then(sender.bind(BindingSpecification.binding(
                                            dlxName, b.routingKey(), dlqName)));
                        }))
                .then()
                .block();
    }
}