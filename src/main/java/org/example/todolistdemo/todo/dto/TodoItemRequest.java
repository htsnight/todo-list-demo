package org.example.todolistdemo.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;
import org.example.todolistdemo.todo.model.CategoryPreset;
import org.example.todolistdemo.todo.model.ReminderRecurrence;
import org.example.todolistdemo.todo.model.TodoPriority;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

public record TodoItemRequest(
        @NotBlank(message = "标题不能为空")
        @Size(max = 120, message = "标题长度不能超过120个字符")
        String title,
        @Size(max = 2000, message = "描述长度不能超过2000个字符")
        String description,
        @Size(max = 40, message = "分类长度不能超过40个字符")
        String category,
        CategoryPreset presetCategory,
        TodoPriority priority,
        LocalDate dueDate,
        OffsetDateTime reminderAt,
        List<@PositiveOrZero(message = "提醒偏移必须为非负数") Integer> reminderOffsetsMinutes,
        ReminderRecurrence recurrence,
        @PositiveOrZero(message = "重复间隔需为非负数") Integer recurrenceIntervalMinutes,
        DayOfWeek recurrenceDayOfWeek,
        LocalTime recurrenceTime
) {
}

