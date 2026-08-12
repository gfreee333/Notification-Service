package ru.bank.notification_service.config;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.bank.notification_service.event.AuthEvent;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.group-id}")
    private String defualtGroupId;

    @Bean
    public <T> ConsumerFactory<String, T> createConsumerFactory(
            Class<T> targetClass,
            Map<String, Object> extraProperty
    ){
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, defualtGroupId);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        if(extraProperty != null){
            config.putAll(extraProperty);
        }
        JsonDeserializer<T> jsonDeserializer = new JsonDeserializer<>(targetClass);
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeMapperForKey(true);
        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                jsonDeserializer
        );
    }

    /**
     * Базовая фабрика создания Consumer для чтения Event от Auth-Service
     */
    @Bean
    public ConsumerFactory<String, AuthEvent> authEventConsumerFactory(){
        return createConsumerFactory(
                AuthEvent.class,
                Map.of(
                        ConsumerConfig.MAX_POLL_RECORDS_CONFIG,10,
                        ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 45000,
                        ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000,
                        ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000
                )
        );
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AuthEvent> authEventKafkaListenerContainerFactory(){
        ConcurrentKafkaListenerContainerFactory<String, AuthEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(authEventConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setConcurrency(3);
        return factory;
    }

}
