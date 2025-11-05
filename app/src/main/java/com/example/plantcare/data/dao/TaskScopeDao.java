package com.example.plantcare.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.plantcare.data.entity.TaskScope;

import java.util.List;

@Dao
public interface TaskScopeDao {

    @Insert
    void insert(TaskScope taskScope);

    @Query("DELETE FROM TaskScope WHERE plantId = :plantId AND taskId = :taskId")
    void delete(int plantId, int taskId);

    @Query("SELECT * FROM TaskScope WHERE plantId = :plantId")
    List<TaskScope> getTasksByPlantId(int plantId);

    @Query("SELECT * FROM TaskScope WHERE taskId = :taskId")
    List<TaskScope> getPlantsByTaskId(int taskId);
}
