/*
 * Author: 海天石
 * Email : htsnight@163.com
 */
package org.example.todolistdemo.todo.model;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "todo_items")
public class TodoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false, length = 40)
    private String category = "general";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TodoPriority priority = TodoPriority.MEDIUM;

    private LocalDate dueDate;

    private OffsetDateTime reminderAt;

    @ElementCollection
    @CollectionTable(name = "todo_reminder_offsets", joinColumns = @JoinColumn(name = "todo_id"))
    @Column(name = "offset_minutes")
    private List<Integer> reminderOffsetsMinutes = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ReminderRecurrence recurrence = ReminderRecurrence.NONE;

    private Integer recurrenceIntervalMinutes;

    private DayOfWeek recurrenceDayOfWeek;

    private LocalTime recurrenceTime;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TodoPriority getPriority() {
        return priority;
    }

    public void setPriority(TodoPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public OffsetDateTime getReminderAt() {
        return reminderAt;
    }

    public void setReminderAt(OffsetDateTime reminderAt) {
        this.reminderAt = reminderAt;
    }

    public List<Integer> getReminderOffsetsMinutes() {
        return reminderOffsetsMinutes;
    }

    public void setReminderOffsetsMinutes(List<Integer> reminderOffsetsMinutes) {
        if (reminderOffsetsMinutes == null) {
            this.reminderOffsetsMinutes = new ArrayList<>();
        } else {
            this.reminderOffsetsMinutes = new ArrayList<>(reminderOffsetsMinutes);
        }
    }

    public ReminderRecurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(ReminderRecurrence recurrence) {
        this.recurrence = recurrence;
    }

    public Integer getRecurrenceIntervalMinutes() {
        return recurrenceIntervalMinutes;
    }

    public void setRecurrenceIntervalMinutes(Integer recurrenceIntervalMinutes) {
        this.recurrenceIntervalMinutes = recurrenceIntervalMinutes;
    }

    public DayOfWeek getRecurrenceDayOfWeek() {
        return recurrenceDayOfWeek;
    }

    public void setRecurrenceDayOfWeek(DayOfWeek recurrenceDayOfWeek) {
        this.recurrenceDayOfWeek = recurrenceDayOfWeek;
    }

    public LocalTime getRecurrenceTime() {
        return recurrenceTime;
    }

    public void setRecurrenceTime(LocalTime recurrenceTime) {
        this.recurrenceTime = recurrenceTime;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}

