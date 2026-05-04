package com.example.taskapi.service;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.kafka.TaskEvent;
import com.example.taskapi.kafka.TaskEvent.EventType;
import com.example.taskapi.kafka.TaskEventProducer;
import com.example.taskapi.model.Task;
import com.example.taskapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventProducer taskEventProducer;

    public List<TaskResponse> getAllTasks(String userId) {
        List<Task> tasks = (userId != null)
                ? taskRepository.findByUserId(userId)
                : taskRepository.findAll();
        return tasks.stream().map(this::toResponse).toList();
    }

    public TaskResponse getTaskById(Long id, String userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        if (userId != null && !userId.equals(task.getUserId())) {
            throw new TaskNotFoundException(id);
        }
        return toResponse(task);
    }

    public TaskResponse createTask(TaskRequest request, String userId) {
        Task task = new Task(request.title(), request.description(), request.completed(), userId);
        Task saved = taskRepository.save(task);
        publishEvent(saved, EventType.CREATED);
        return toResponse(saved);
    }

    public TaskResponse updateTask(Long id, TaskRequest request, String userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        if (userId != null && !userId.equals(task.getUserId())) {
            throw new TaskNotFoundException(id);
        }
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setCompleted(request.completed());
        Task updated = taskRepository.save(task);
        publishEvent(updated, EventType.UPDATED);
        return toResponse(updated);
    }

    public void deleteTask(Long id, String userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        if (userId != null && !userId.equals(task.getUserId())) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
        publishEvent(task, EventType.DELETED);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.isCompleted());
    }

    private void publishEvent(Task task, EventType eventType) {
        TaskEvent event = new TaskEvent(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.isCompleted(),
                eventType
        );
        taskEventProducer.sendEvent(event);
    }
}
