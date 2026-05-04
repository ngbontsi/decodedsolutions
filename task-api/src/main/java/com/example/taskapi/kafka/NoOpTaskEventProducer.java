package com.example.taskapi.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "false", matchIfMissing = true)
public class NoOpTaskEventProducer implements TaskEventProducer {

    @Override
    public void sendEvent(TaskEvent event) {
        log.debug("Kafka disabled - skipping event: {} for task id: {}", event.eventType(), event.id());
    }
}
