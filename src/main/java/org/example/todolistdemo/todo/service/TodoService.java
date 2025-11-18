package org.example.todolistdemo.todo.service;

import org.example.todolistdemo.todo.dto.TodoItemRequest;
import org.example.todolistdemo.todo.dto.TodoItemResponse;
import org.example.todolistdemo.todo.exception.TodoNotFoundException;
import org.example.todolistdemo.todo.model.CategoryPreset;
import org.example.todolistdemo.todo.model.TodoItem;
import org.example.todolistdemo.todo.model.TodoPriority;
import org.example.todolistdemo.todo.repository.TodoItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TodoService {

    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    public List<TodoItemResponse> findAll(String sortBy, Sort.Direction direction) {
        Sort sort = Sort.by(direction, mapSortField(sortBy));
        return repository.findAll(sort).stream().map(this::toResponse).toList();
    }

    @Transactional
    public TodoItemResponse create(TodoItemRequest request) {
        TodoItem entity = new TodoItem();
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setCategory(resolveCategory(request.category(), request.presetCategory()));
        entity.setPriority(TodoPriority.fromNullable(request.priority()));
        entity.setDueDate(request.dueDate());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public TodoItemResponse toggleCompletion(Long id) {
        TodoItem entity = repository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        entity.setCompleted(!entity.isCompleted());
        return toResponse(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        repository.deleteById(id);
    }

    private TodoItemResponse toResponse(TodoItem entity) {
        return new TodoItemResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.isCompleted(),
                entity.getCategory(),
                entity.getPriority(),
                entity.getDueDate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private String mapSortField(String sortBy) {
        return switch (sortBy) {
            case "priority" -> "priority";
            case "dueDate" -> "dueDate";
            case "updatedAt" -> "updatedAt";
            case "createdAt" -> "createdAt";
            default -> throw new IllegalArgumentException("不支持的排序字段: " + sortBy);
        };
    }

    private String resolveCategory(String category, CategoryPreset preset) {
        if (preset != null) {
            return preset.label();
        }
        if (category == null || category.isBlank()) {
            return "general";
        }
        return category.trim();
    }
}

