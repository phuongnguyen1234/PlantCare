package com.example.plantcare.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;

import java.time.LocalDateTime;

@Entity(tableName = "Task")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int taskId;

    @NonNull
    private String name = "Công việc của tôi";

    @NonNull
    private TaskType type = TaskType.WATER;

    private int frequency = 1;

    @NonNull
    private FrequencyUnit frequencyUnit = FrequencyUnit.HOUR;

    private LocalDateTime notifyStart;
    private LocalDateTime notifyEnd;
    private boolean isRepeat;

    @NonNull
    private Status status = Status.SCHEDULED;
    private LocalDateTime nextDue;

    @NonNull
    private LocalDateTime notifyTime = LocalDateTime.now();
    private String note;

    public Task() {
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public TaskType getType() {
        return type;
    }

    public void setType(@NonNull TaskType type) {
        this.type = type;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public LocalDateTime getNotifyStart() {
        return notifyStart;
    }

    public void setNotifyStart(LocalDateTime notifyStart) {
        this.notifyStart = notifyStart;
    }

    public LocalDateTime getNotifyEnd() {
        return notifyEnd;
    }

    public void setNotifyEnd(LocalDateTime notifyEnd) {
        this.notifyEnd = notifyEnd;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@NonNull Status status) {
        this.status = status;
    }

    public LocalDateTime getNextDue() {
        return nextDue;
    }

    public void setNextDue(LocalDateTime nextDue) {
        this.nextDue = nextDue;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    @NonNull
    public LocalDateTime getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(@NonNull LocalDateTime notifyTime) {
        this.notifyTime = notifyTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @NonNull
    public FrequencyUnit getFrequencyUnit() {
        return frequencyUnit;
    }

    public void setFrequencyUnit(@NonNull FrequencyUnit frequencyUnit) {
        this.frequencyUnit = frequencyUnit;
    }
}
