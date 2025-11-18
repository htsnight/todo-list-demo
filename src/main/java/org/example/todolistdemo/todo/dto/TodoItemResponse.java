package org.example.todolistdemo.todo.dto;

import org.example.todolistdemo.todo.model.TodoPriority;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TodoItemResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        String category,
        TodoPriority priority,
        LocalDate dueDate,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

