package org.example.todolistdemo.todo.dto;

import java.time.OffsetDateTime;

public record TodoItemResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

