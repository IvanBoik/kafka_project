package com.boiko.data_service.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic songsTopic() {
        return TopicBuilder
                .name("songsTopic")
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic albumsTopic() {
        return TopicBuilder
                .name("albumsTopic")
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic authorsTopic() {
        return TopicBuilder
                .name("authorsTopic")
                .partitions(1)
                .build();
    }

    @Bean
    public NewTopic auditionsTopic() {
        return TopicBuilder
                .name("auditionsTopic")
                .partitions(2)
                .build();
    }
}
