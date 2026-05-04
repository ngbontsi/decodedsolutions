package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Test Task", "Test Description", false);
        taskRequest = new TaskRequest("Test Task", "Test Description", false);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(task));
        List<TaskResponse> result = taskService.getAllTasks();
        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).title());
        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        TaskResponse result = taskService.getTaskById(1L);
        assertEquals("Test Task", result.title());
        assertEquals("Test Description", result.description());
        assertFalse(result.completed());
    }

    @Test
    void getTaskById_WhenNotExists_ShouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(99L));
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        TaskResponse result = taskService.createTask(taskRequest);
        assertNotNull(result);
        assertEquals("Test Task", result.title());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_WhenExists_ShouldUpdateAndReturn() {
        TaskRequest updateRequest = new TaskRequest("Updated Task", "Updated Desc", true);
        Task updatedTask = new Task(1L, "Updated Task", "Updated Desc", true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        TaskResponse result = taskService.updateTask(1L, updateRequest);
        assertEquals("Updated Task", result.title());
        assertTrue(result.completed());
    }

    @Test
    void updateTask_WhenNotExists_ShouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(99L, taskRequest));
    }

    @Test
    void deleteTask_WhenExists_ShouldDelete() {
        when(taskRepository.existsById(1L)).thenReturn(true);
        taskService.deleteTask(1L);
        verify(taskRepository).deleteById(1L);
    }

    @Test
    void deleteTask_WhenNotExists_ShouldThrowException() {
        when(taskRepository.existsById(99L)).thenReturn(false);
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(99L));
    }
}
