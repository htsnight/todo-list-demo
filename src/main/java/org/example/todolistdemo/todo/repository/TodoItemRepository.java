package org.example.todolistdemo.todo.repository;

import org.example.todolistdemo.todo.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
    List<TodoItem> findByCompletedFalseAndReminderAtBetween(OffsetDateTime start, OffsetDateTime end);

    List<TodoItem> findByCompletedFalse();
}

