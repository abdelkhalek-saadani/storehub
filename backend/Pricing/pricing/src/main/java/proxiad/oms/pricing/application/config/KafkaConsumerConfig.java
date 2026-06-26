package proxiad.oms.pricing.application.config;


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
import com.proxiad.events.CartChangedEvent;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    public ConsumerFactory<String, CartChangedEvent> cartChangedConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "pricing-changed");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "proxiad.oms.pricing.application.domain.events");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS,"false");
//        props.put(JsonDeserializer.TYPE_MAPPINGS,
//                "proxiad.oms.pricing.application.models.CartChangedEvent:" +
//                        "proxiad.oms.pricing.application.models.CartChangedEvent");
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(CartChangedEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CartChangedEvent> cartChangedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CartChangedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(cartChangedConsumerFactory());
        return factory;
    }

}
