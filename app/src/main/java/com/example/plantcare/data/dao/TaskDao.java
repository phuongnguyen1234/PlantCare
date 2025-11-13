package com.example.plantcare.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.model.TaskWithPlants;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Transaction
    @Query("SELECT * FROM Task ORDER BY notifyTime ASC")
    LiveData<List<TaskWithPlants>> getAllTasksWithPlants();

    @Query("SELECT * FROM Task WHERE taskId = :id LIMIT 1")
    LiveData<Task> getTaskById(int id);

    @Query("SELECT * FROM Task WHERE status = :status")
    LiveData<List<Task>> getTasksByStatus(String status);

    @Query("SELECT COUNT(*) FROM Task WHERE status != 'COMPLETED'")
    LiveData<Integer> getActiveTaskCount();

    @Query("SELECT COUNT(*) FROM Task WHERE status != 'COMPLETED' AND expiration BETWEEN :from AND :to")
    LiveData<Integer> getDueSoonTaskCount(LocalDateTime from, LocalDateTime to);

    // Thêm các hàm sync (cho AlarmManager, Worker, Receiver)
    @Query("SELECT * FROM Task ORDER BY notifyTime ASC")
    List<Task> getAllTasksSync();

    @Query("SELECT * FROM Task WHERE taskId = :id LIMIT 1")
    Task getTaskSync(int id);
}
