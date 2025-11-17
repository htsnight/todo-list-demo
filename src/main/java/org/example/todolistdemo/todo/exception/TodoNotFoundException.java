package org.example.todolistdemo.todo.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("未找到 ID 为 " + id + " 的待办事项");
    }
}

