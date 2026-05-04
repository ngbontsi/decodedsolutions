package com.example.taskapi.controller;

import com.example.taskapi.dto.TaskRequest;
import com.example.taskapi.dto.TaskResponse;
import com.example.taskapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Task", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves a list of all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tasks",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class))))
    })
    public List<TaskResponse> getAllTasks(
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return taskService.getAllTasks(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a single task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content)
    })
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.ok(taskService.getTaskById(id, userId));
    }

    @PostMapping
    @Operation(summary = "Create a new task", description = "Creates a new task with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Updates an existing task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        return ResponseEntity.ok(taskService.updateTask(id, request, userId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }
}
