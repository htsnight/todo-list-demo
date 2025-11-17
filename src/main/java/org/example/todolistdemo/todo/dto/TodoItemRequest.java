package org.example.todolistdemo.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TodoItemRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 120, message = "标题长度不能超过120个字符")
        String title,
        @Size(max = 2000, message = "描述长度不能超过2000个字符")
        String description
) {
}

