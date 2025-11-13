package com.example.plantcare.data.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.entity.TaskScope;

import java.util.List;

public class TaskWithPlants {
    @Embedded
    public Task task;

    @Relation(
            parentColumn = "taskId", // ID of the parent (Task)
            entityColumn = "plantId", // ID of the entity being related (Plant)
            associateBy = @Junction(
                    value = TaskScope.class,
                    parentColumn = "taskId", // Column in TaskScope that points to Task
                    entityColumn = "plantId"  // Column in TaskScope that points to Plant
            )
    )
    public List<Plant> plants;
}
