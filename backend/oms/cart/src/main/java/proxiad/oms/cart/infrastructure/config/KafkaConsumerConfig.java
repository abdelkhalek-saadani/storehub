package proxiad.oms.cart.infrastructure.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import proxiad.oms.cart.infrastructure.models.TotalCalculatedEvent;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    public ConsumerFactory<String, TotalCalculatedEvent> totalCalculatedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "greeting");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "proxiad.oms.pricing.events");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,"false");
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(TotalCalculatedEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TotalCalculatedEvent> totalCalculatedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TotalCalculatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(totalCalculatedConsumerFactory());
        return factory;
    }

}
