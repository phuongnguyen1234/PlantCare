package com.example.plantcare.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity(tableName = "History")
public class History {
    @PrimaryKey(autoGenerate = true)
    private int historyId;

    @NonNull
    private String taskName = "Công việc của tôi";

    @NonNull
    private TaskType taskType = TaskType.WATER;

    @NonNull
    private Status status = Status.MISSED;

    @NonNull
    private String content = "Nội dung lịch sử";

    @NonNull
    private LocalDateTime notifyTime = LocalDateTime.now().minusHours(2);
    private LocalDateTime dateCompleted;

    public History() {
    }

    public int getHistoryId() {
        return historyId;
    }

    public void setHistoryId(int historyId) {
        this.historyId = historyId;
    }

    @NonNull
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(@NonNull String taskName) {
        this.taskName = taskName;
    }

    @NonNull
    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(@NonNull TaskType taskType) {
        this.taskType = taskType;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    public void setStatus(@NonNull Status status) {
        this.status = status;
    }

    @NonNull
    public String getContent() {
        return content;
    }

    public void setContent(@NonNull String content) {
        this.content = content;
    }

    @NonNull
    public LocalDateTime getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(@NonNull LocalDateTime notifyTime) {
        this.notifyTime = notifyTime;
    }

    public LocalDateTime getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(LocalDateTime dateCompleted) {
        this.dateCompleted = dateCompleted;
    }
    // thêm Date formater phương thức từ đây _-----
    @Ignore
    private static final DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");

    public String getFormattedNotifyTime() {
        if (notifyTime == null) return "";
        return notifyTime.format(formatter);
    }

    public String getFormattedCompleteTime() {
        if (dateCompleted == null) return "";
        return dateCompleted.format(formatter);
    }
}