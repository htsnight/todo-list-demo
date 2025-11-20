package org.example.todolistdemo.todo.service;

import org.example.todolistdemo.todo.dto.CategoryOptionResponse;
import org.example.todolistdemo.todo.model.CategoryPreset;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CategoryCatalog {

    public List<CategoryOptionResponse> presets() {
        return Arrays.stream(CategoryPreset.values())
                .map(preset -> new CategoryOptionResponse(preset.code(), preset.label()))
                .toList();
    }
}

