package com.example.taskapi.repository;

import com.example.taskapi.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void save_ShouldPersistTask() {
        Task task = new Task("Test", "Description", false);
        Task saved = taskRepository.save(task);
        assertNotNull(saved.getId());
        assertEquals("Test", saved.getTitle());
    }

    @Test
    void findAll_ShouldReturnAllTasks() {
        taskRepository.save(new Task("Task 1", "Desc 1", false));
        taskRepository.save(new Task("Task 2", "Desc 2", true));
        List<Task> tasks = taskRepository.findAll();
        assertEquals(2, tasks.size());
    }

    @Test
    void findById_WhenExists_ShouldReturnTask() {
        Task task = taskRepository.save(new Task("Test", "Desc", false));
        Optional<Task> found = taskRepository.findById(task.getId());
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getTitle());
    }

    @Test
    void findById_WhenNotExists_ShouldReturnEmpty() {
        Optional<Task> found = taskRepository.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    void deleteById_ShouldRemoveTask() {
        Task task = taskRepository.save(new Task("Test", "Desc", false));
        taskRepository.deleteById(task.getId());
        assertFalse(taskRepository.findById(task.getId()).isPresent());
    }

    @Test
    void update_ShouldModifyTask() {
        Task task = taskRepository.save(new Task("Original", "Desc", false));
        task.setTitle("Updated");
        task.setCompleted(true);
        Task updated = taskRepository.save(task);
        assertEquals("Updated", updated.getTitle());
        assertTrue(updated.isCompleted());
    }
}
