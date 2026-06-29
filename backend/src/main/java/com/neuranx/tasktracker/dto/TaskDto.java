package com.neuranx.tasktracker.dto;

import com.neuranx.tasktracker.entity.enums.Priority;
import com.neuranx.tasktracker.entity.enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDto {

    // ─── Request ───────────────────────────────────────────────────────────────

    public record CreateRequest(
            @NotBlank(message = "Title must not be blank")
            @Size(max = 255, message = "Title must be 255 characters or fewer")
            String title,

            String description,

            Status status,   // defaults to TODO in service if null

            Priority priority, // defaults to MEDIUM in service if null

            LocalDate dueDate,

            Long projectId   // optional; task created without project if null
    ) {}

    public record UpdateRequest(
            @NotBlank(message = "Title must not be blank")
            @Size(max = 255, message = "Title must be 255 characters or fewer")
            String title,

            String description,

            Status status,

            Priority priority,

            LocalDate dueDate,

            Long projectId
    ) {}

    // ─── Response ──────────────────────────────────────────────────────────────

    public record Response(
            Long id,
            String title,
            String description,
            Status status,
            Priority priority,
            LocalDate dueDate,
            Long projectId,
            String projectName,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
