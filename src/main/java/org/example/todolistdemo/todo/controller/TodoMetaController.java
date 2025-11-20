package org.example.todolistdemo.todo.controller;

import org.example.todolistdemo.todo.dto.CategoryOptionResponse;
import org.example.todolistdemo.todo.service.CategoryCatalog;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/todos/meta")
public class TodoMetaController {

    private final CategoryCatalog categoryCatalog;

    public TodoMetaController(CategoryCatalog categoryCatalog) {
        this.categoryCatalog = categoryCatalog;
    }

    @GetMapping("/categories")
    public List<CategoryOptionResponse> categoryOptions() {
        return categoryCatalog.presets();
    }
}

