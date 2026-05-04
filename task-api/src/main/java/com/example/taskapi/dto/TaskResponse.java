package com.example.taskapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Task response")
public record TaskResponse(
        @Schema(description = "Task ID", example = "1")
        Long id,

        @Schema(description = "Task title", example = "Complete project")
        String title,

        @Schema(description = "Task description", example = "Finish the implementation")
        String description,

        @Schema(description = "Completion status", example = "false")
        boolean completed
) {}
