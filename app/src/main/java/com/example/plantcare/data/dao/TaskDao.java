package com.example.plantcare.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.plantcare.data.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface TaskDao {

    @Insert
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("SELECT * FROM Task ORDER BY nextDue ASC")
    LiveData<List<Task>> getAllTasks();

    @Query("SELECT * FROM Task WHERE taskId = :id LIMIT 1")
    LiveData<Task> getTaskById(int id);

    @Query("SELECT * FROM Task WHERE status = :status")
    LiveData<List<Task>> getTasksByStatus(String status);

    @Query("SELECT COUNT(*) FROM Task WHERE status != 'COMPLETED'")
    LiveData<Integer> getActiveTaskCount();

    @Query("SELECT COUNT(*) FROM Task WHERE status != 'COMPLETED' AND nextDue BETWEEN :from AND :to")
    LiveData<Integer> getDueSoonTaskCount(LocalDateTime from, LocalDateTime to);
}
