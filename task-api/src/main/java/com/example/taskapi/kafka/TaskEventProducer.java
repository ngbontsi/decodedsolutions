package com.example.taskapi.kafka;

public interface TaskEventProducer {
    void sendEvent(TaskEvent event);
}
