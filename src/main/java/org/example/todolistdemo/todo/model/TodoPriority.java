package org.example.todolistdemo.todo.model;

public enum TodoPriority {
    LOW,
    MEDIUM,
    HIGH;

    public static TodoPriority fromNullable(TodoPriority value) {
        return value == null ? MEDIUM : value;
    }
}

