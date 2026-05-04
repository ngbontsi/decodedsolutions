package com.example.taskapi.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!skip-kafka")
public class KafkaConfig {
    // Kafka configuration is managed via application.properties
    // This class can be used for custom Kafka settings if needed
}
