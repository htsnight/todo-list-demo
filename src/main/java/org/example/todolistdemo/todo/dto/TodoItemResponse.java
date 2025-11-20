package org.example.todolistdemo.todo.dto;

import org.example.todolistdemo.todo.model.ReminderRecurrence;
import org.example.todolistdemo.todo.model.TodoPriority;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

public record TodoItemResponse(
        Long id,
        String title,
        String description,
        boolean completed,
        String category,
        TodoPriority priority,
        LocalDate dueDate,
        OffsetDateTime reminderAt,
        List<Integer> reminderOffsetsMinutes,
        ReminderRecurrence recurrence,
        Integer recurrenceIntervalMinutes,
        DayOfWeek recurrenceDayOfWeek,
        LocalTime recurrenceTime,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

