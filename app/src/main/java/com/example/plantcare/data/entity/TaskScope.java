package com.example.plantcare.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
        tableName = "TaskScope",
        primaryKeys = {"plantId", "taskId"},
        foreignKeys = {
                @ForeignKey(entity = Plant.class,
                        parentColumns = "plantId",
                        childColumns = "plantId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Task.class,
                        parentColumns = "taskId",
                        childColumns = "taskId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {
                @Index(value = {"plantId"}),
                @Index(value = {"taskId"})
        }
)
public class TaskScope {
    private int plantId;
    private int taskId;

    public TaskScope() {
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
