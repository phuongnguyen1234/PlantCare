package com.example.plantcare.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.plantcare.data.entity.Task;

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
    List<Task> getAllTasks();

    @Query("SELECT * FROM Task WHERE taskId = :id LIMIT 1")
    Task getTaskById(int id);

    @Query("SELECT * FROM Task WHERE status = :status")
    List<Task> getTasksByStatus(String status);
}
