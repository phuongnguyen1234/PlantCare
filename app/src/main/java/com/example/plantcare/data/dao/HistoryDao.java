package com.example.plantcare.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.plantcare.data.entity.History;

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
    List<History> getAllHistory();

    @Query("SELECT * FROM History WHERE status = :status ORDER BY dateCompleted DESC")
    List<History> getHistoryByStatus(String status);
}
