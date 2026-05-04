package com.example.taskapi.kafka;

public record TaskEvent(
        Long id,
        String title,
        String description,
        boolean completed,
        EventType eventType
) {
    public enum EventType {
        CREATED, UPDATED, DELETED
    }
}
