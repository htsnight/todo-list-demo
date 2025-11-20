/*
 * Author: 海天石
 * Email : htsnight@163.com
 */
package org.example.todolistdemo.todo.service;

import org.example.todolistdemo.todo.dto.TodoItemRequest;
import org.example.todolistdemo.todo.dto.TodoItemResponse;
import org.example.todolistdemo.todo.exception.TodoNotFoundException;
import org.example.todolistdemo.todo.model.CategoryPreset;
import org.example.todolistdemo.todo.model.ReminderRecurrence;
import org.example.todolistdemo.todo.model.TodoItem;
import org.example.todolistdemo.todo.model.TodoPriority;
import org.example.todolistdemo.todo.repository.TodoItemRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
        entity.setReminderAt(request.reminderAt());
        entity.setReminderOffsetsMinutes(normalizeOffsets(request.reminderOffsetsMinutes()));
        ReminderRecurrence recurrence = Objects.requireNonNullElse(request.recurrence(), ReminderRecurrence.NONE);
        validateRecurrenceConfig(recurrence, request);
        entity.setRecurrence(recurrence);
        entity.setRecurrenceIntervalMinutes(
                recurrence == ReminderRecurrence.INTERVAL ? normalizeInterval(request.recurrenceIntervalMinutes()) : null);
        entity.setRecurrenceTime(recurrence == ReminderRecurrence.NONE ? null : request.recurrenceTime());
        entity.setRecurrenceDayOfWeek(recurrence == ReminderRecurrence.WEEKLY ? request.recurrenceDayOfWeek() : null);
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

    public List<TodoItemResponse> upcomingReminders(long minutesAhead) {
        if (minutesAhead <= 0) {
            throw new IllegalArgumentException("minutes 参数必须大于 0");
        }
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime end = now.plusMinutes(minutesAhead);
        Map<Long, TodoItem> candidates = new LinkedHashMap<>();

        repository.findByCompletedFalseAndReminderAtBetween(now, end)
                .forEach(item -> candidates.put(item.getId(), item));

        for (TodoItem item : repository.findByCompletedFalse()) {
            if (item.getReminderAt() != null) {
                for (Integer offset : item.getReminderOffsetsMinutes()) {
                    if (offset == null) continue;
                    OffsetDateTime candidate = item.getReminderAt().minusMinutes(offset);
                    if (isWithinWindow(candidate, now, end)) {
                        candidates.putIfAbsent(item.getId(), item);
                        break;
                    }
                }
            }
            OffsetDateTime recurrence = nextRecurrenceOccurrence(item, now);
            if (recurrence != null && isWithinWindow(recurrence, now, end)) {
                candidates.putIfAbsent(item.getId(), item);
            }
        }

        return candidates.values().stream().map(this::toResponse).toList();
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
                entity.getReminderAt(),
                entity.getReminderOffsetsMinutes(),
                entity.getRecurrence(),
                entity.getRecurrenceIntervalMinutes(),
                entity.getRecurrenceDayOfWeek(),
                entity.getRecurrenceTime(),
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

    private List<Integer> normalizeOffsets(List<Integer> offsets) {
        if (offsets == null) {
            return List.of();
        }
        return offsets.stream()
                .filter(Objects::nonNull)
                .filter(value -> value >= 0)
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean isWithinWindow(OffsetDateTime candidate, OffsetDateTime start, OffsetDateTime end) {
        return candidate != null && !candidate.isBefore(start) && !candidate.isAfter(end);
    }

    private OffsetDateTime nextRecurrenceOccurrence(TodoItem item, OffsetDateTime reference) {
        ReminderRecurrence recurrence = item.getRecurrence();
        if (recurrence == null || recurrence == ReminderRecurrence.NONE) {
            return null;
        }
        return switch (recurrence) {
            case DAILY -> {
                LocalTime time = item.getRecurrenceTime();
                yield time == null ? null : nextDailyOccurrence(reference, time);
            }
            case WEEKLY -> {
                LocalTime time = item.getRecurrenceTime();
                yield time == null ? null : nextWeeklyOccurrence(reference, time, item.getRecurrenceDayOfWeek());
            }
            case INTERVAL -> nextIntervalOccurrence(item, reference);
            case NONE -> null;
        };
    }

    private OffsetDateTime nextIntervalOccurrence(TodoItem item, OffsetDateTime reference) {
        OffsetDateTime start = item.getReminderAt();
        Integer interval = item.getRecurrenceIntervalMinutes();
        if (start == null || interval == null || interval <= 0) {
            return null;
        }
        if (reference.isBefore(start)) {
            return start;
        }
        long minutesPassed = Duration.between(start, reference).toMinutes();
        long steps = minutesPassed / interval + 1;
        return start.plusMinutes(steps * interval);
    }

    private OffsetDateTime nextDailyOccurrence(OffsetDateTime reference, LocalTime time) {
        OffsetDateTime candidate = withTime(reference, time);
        if (candidate.isBefore(reference)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    private OffsetDateTime nextWeeklyOccurrence(OffsetDateTime reference, LocalTime time, DayOfWeek targetDay) {
        if (targetDay == null) {
            return null;
        }
        int current = reference.getDayOfWeek().getValue();
        int target = targetDay.getValue();
        int daysAhead = target - current;
        if (daysAhead < 0) {
            daysAhead += 7;
        }
        OffsetDateTime candidate = withTime(reference, time).plusDays(daysAhead);
        if (candidate.isBefore(reference)) {
            candidate = candidate.plusWeeks(1);
        }
        return candidate;
    }

    private OffsetDateTime withTime(OffsetDateTime reference, LocalTime time) {
        return reference
                .withHour(time.getHour())
                .withMinute(time.getMinute())
                .withSecond(0)
                .withNano(0);
    }

    private void validateRecurrenceConfig(ReminderRecurrence recurrence, TodoItemRequest request) {
        switch (recurrence) {
            case NONE -> {
            }
            case INTERVAL -> {
                if (request.reminderAt() == null) {
                    throw new IllegalArgumentException("循环提醒需要设置初始提醒时间");
                }
                if (request.recurrenceIntervalMinutes() == null || request.recurrenceIntervalMinutes() <= 0) {
                    throw new IllegalArgumentException("循环提醒的间隔必须大于 0");
                }
            }
            case DAILY -> {
                if (request.recurrenceTime() == null) {
                    throw new IllegalArgumentException("每日提醒需要设置提醒时间");
                }
            }
            case WEEKLY -> {
                if (request.recurrenceTime() == null || request.recurrenceDayOfWeek() == null) {
                    throw new IllegalArgumentException("每周提醒需要设置星期与时间");
                }
            }
        }
    }

    private Integer normalizeInterval(Integer minutes) {
        if (minutes == null || minutes <= 0) {
            return null;
        }
        return minutes;
    }
}

