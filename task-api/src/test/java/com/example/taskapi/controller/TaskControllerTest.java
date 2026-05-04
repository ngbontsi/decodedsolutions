package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.exception.GlobalExceptionHandler;
import com.example.taskapi.exception.TaskNotFoundException;
import com.example.taskapi.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @Test
    void getAllTasks_ShouldReturnTasks() throws Exception {
        TaskResponse task = new TaskResponse(1L, "Test Task", "Description", false);
        when(taskService.getAllTasks()).thenReturn(List.of(task));
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"));
    }

    @Test
    void getTaskById_WhenExists_ShouldReturnTask() throws Exception {
        TaskResponse task = new TaskResponse(1L, "Test Task", "Description", false);
        when(taskService.getTaskById(1L)).thenReturn(task);
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getTaskById_WhenNotExists_ShouldReturn404() throws Exception {
        when(taskService.getTaskById(99L)).thenThrow(new TaskNotFoundException(99L));
        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createTask_WithValidData_ShouldReturn201() throws Exception {
        TaskRequest request = new TaskRequest("New Task", "Description", false);
        TaskResponse response = new TaskResponse(1L, "New Task", "Description", false);
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    void createTask_WithBlankTitle_ShouldReturn400() throws Exception {
        TaskRequest request = new TaskRequest("", "Description", false);
        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").exists());
    }

    @Test
    void updateTask_WithValidData_ShouldReturn200() throws Exception {
        TaskRequest request = new TaskRequest("Updated", "Desc", true);
        TaskResponse response = new TaskResponse(1L, "Updated", "Desc", true);
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(response);
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTask_WhenExists_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
