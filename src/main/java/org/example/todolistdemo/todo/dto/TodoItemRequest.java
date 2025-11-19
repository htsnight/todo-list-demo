package org.example.todolistdemo.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.todolistdemo.todo.model.CategoryPreset;
import org.example.todolistdemo.todo.model.TodoPriority;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record TodoItemRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 120, message = "标题长度不能超过120个字符")
        String title,
        @Size(max = 2000, message = "描述长度不能超过2000个字符")
        String description,
        @Size(max = 40, message = "分类长度不能超过40个字符")
        String category,
        CategoryPreset presetCategory,
        TodoPriority priority,
        LocalDate dueDate,
        OffsetDateTime reminderAt
) {
}

