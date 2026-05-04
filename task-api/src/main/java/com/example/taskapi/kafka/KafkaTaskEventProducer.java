package com.example.taskapi.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true", matchIfMissing = false)
public class KafkaTaskEventProducer implements TaskEventProducer {

    private static final String TOPIC = "task-events";
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public KafkaTaskEventProducer(KafkaTemplate<String, TaskEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendEvent(TaskEvent event) {
        try {
            String key = String.valueOf(event.id());
            kafkaTemplate.send(TOPIC, key, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Event sent: {} for task id: {}", event.eventType(), event.id());
                        } else {
                            log.error("Failed to send event for task id: {}", event.id(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending task event", e);
        }
    }
}
