package com.example.taskapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Task creation/update request")
public record TaskRequest(
        @Schema(description = "Task title", example = "Complete project")
        @NotBlank(message = "Title is required")
        String title,

        @Schema(description = "Task description", example = "Finish the Spring Boot implementation")
        String description,

        @Schema(description = "Completion status", example = "false")
        boolean completed
) {}
