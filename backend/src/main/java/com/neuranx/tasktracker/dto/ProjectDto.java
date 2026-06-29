package com.neuranx.tasktracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ProjectDto {

    public record CreateRequest(
            @NotBlank(message = "Name must not be blank")
            @Size(max = 200, message = "Name must be 200 characters or fewer")
            String name,

            String description
    ) {}

    public record Response(
            Long id,
            String name,
            String description,
            int taskCount,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}
