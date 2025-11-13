package com.example.plantcare.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.plantcare.data.entity.TaskScope;

import java.util.List;

@Dao
public interface TaskScopeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TaskScope taskScope);

    @Query("DELETE FROM TaskScope WHERE plantId = :plantId AND taskId = :taskId")
    void delete(int plantId, int taskId);

    @Query("DELETE FROM TaskScope WHERE taskId = :taskId")
    void deleteByTaskId(int taskId);

    @Query("SELECT * FROM TaskScope WHERE plantId = :plantId")
    LiveData<List<TaskScope>> getTasksByPlantId(int plantId);

    @Query("SELECT * FROM TaskScope WHERE taskId = :taskId")
    LiveData<List<TaskScope>> getPlantsByTaskId(int taskId);

    @Query("SELECT * FROM TaskScope WHERE plantId = :plantId")
    LiveData<List<TaskScope>> getTaskScopesByPlantId(int plantId);

    @Transaction
    default void replaceAllByTaskId(int taskId, List<TaskScope> newScopes) {
        deleteByTaskId(taskId);
        for (TaskScope scope : newScopes) {
            insert(scope);
        }
    }
}
