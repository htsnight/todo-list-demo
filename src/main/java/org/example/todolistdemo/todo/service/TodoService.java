package org.example.todolistdemo.todo.service;

import org.example.todolistdemo.todo.dto.TodoItemRequest;
import org.example.todolistdemo.todo.dto.TodoItemResponse;
import org.example.todolistdemo.todo.exception.TodoNotFoundException;
import org.example.todolistdemo.todo.model.TodoItem;
import org.example.todolistdemo.todo.repository.TodoItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TodoService {

    private final TodoItemRepository repository;

    public TodoService(TodoItemRepository repository) {
        this.repository = repository;
    }

    public List<TodoItemResponse> findAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public TodoItemResponse create(TodoItemRequest request) {
        TodoItem entity = new TodoItem();
        entity.setTitle(request.title());
        entity.setDescription(request.description());
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
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

