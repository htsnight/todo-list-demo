package org.example.todolistdemo.todo.model;

public enum CategoryPreset {
    WORK("工作"),
    STUDY("学习"),
    LIFE("生活"),
    HEALTH("健康"),
    PERSONAL("个人提升");

    private final String label;

    CategoryPreset(String label) {
        this.label = label;
    }

    public String code() {
        return name();
    }

    public String label() {
        return label;
    }
}

