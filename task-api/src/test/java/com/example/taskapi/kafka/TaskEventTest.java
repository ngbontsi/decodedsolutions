package com.example.taskapi.kafka;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskEventTest {

    @Test
    void taskEvent_ShouldCreateWithAllFields() {
        TaskEvent event = new TaskEvent(1L, "Test", "Description", false, TaskEvent.EventType.CREATED);
        
        assertEquals(1L, event.id());
        assertEquals("Test", event.title());
        assertEquals("Description", event.description());
        assertFalse(event.completed());
        assertEquals(TaskEvent.EventType.CREATED, event.eventType());
    }

    @Test
    void eventType_ShouldHaveAllValues() {
        assertEquals(3, TaskEvent.EventType.values().length);
        assertNotNull(TaskEvent.EventType.CREATED);
        assertNotNull(TaskEvent.EventType.UPDATED);
        assertNotNull(TaskEvent.EventType.DELETED);
    }
}
