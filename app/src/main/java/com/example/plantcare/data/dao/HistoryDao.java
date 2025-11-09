package com.example.plantcare.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.model.DailyTaskCount;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface HistoryDao {

    @Insert
    long insert(History history);

    @Update
    void update(History history);

    @Delete
    void delete(History history);

    @Query("SELECT * FROM History ORDER BY dateCompleted DESC")
    LiveData<List<History>> getAllHistory();

    @Query("SELECT * FROM History WHERE status = :status ORDER BY dateCompleted DESC")
    LiveData<List<History>> getHistoryByStatus(String status);

    @Query("SELECT COUNT(*) FROM History WHERE dateCompleted >= :since")
    LiveData<Integer> getCompletedSinceCount(LocalDateTime since);

    @Query("SELECT date(dateCompleted) as date, COUNT(*) as count FROM History WHERE dateCompleted >= :since GROUP BY date(dateCompleted)")
    LiveData<List<DailyTaskCount>> getDailyCompletedTaskCounts(LocalDateTime since);

    @Query("SELECT COUNT(*) FROM History WHERE date(dateCompleted) = date('now')")
    LiveData<Integer> getTodayCompletedTaskCount();
}
