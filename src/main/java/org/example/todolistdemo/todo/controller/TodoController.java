package org.example.todolistdemo.todo.controller;

import jakarta.validation.Valid;
import org.example.todolistdemo.todo.dto.TodoItemRequest;
import org.example.todolistdemo.todo.dto.TodoItemResponse;
import org.example.todolistdemo.todo.service.TodoService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoItemResponse> list(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction dir = parseDirection(direction);
        return todoService.findAll(sortBy, dir);
    }

    @GetMapping("/upcoming-reminders")
    public List<TodoItemResponse> upcomingReminders(
            @RequestParam(defaultValue = "60") Long minutes
    ) {
        return todoService.upcomingReminders(minutes);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoItemResponse create(@Valid @RequestBody TodoItemRequest request) {
        return todoService.create(request);
    }

    @PatchMapping("/{id}/toggle")
    public TodoItemResponse toggle(@PathVariable Long id) {
        return todoService.toggleCompletion(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        todoService.delete(id);
    }

    private Sort.Direction parseDirection(String value) {
        try {
            return Sort.Direction.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("direction 仅支持 asc/desc");
        }
    }
}

