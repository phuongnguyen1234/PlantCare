package com.example.plantcare.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.plantcare.data.entity.Journal;

import java.util.List;

@Dao
public interface JournalDao {

    @Insert
    long insert(Journal journal);

    @Update
    void update(Journal journal);

    @Delete
    void delete(Journal journal);

    // Renamed for consistency
    @Query("SELECT * FROM Journal WHERE plantId = :plantId ORDER BY dateCreated DESC")
    LiveData<List<Journal>> getJournalsByPlantId(int plantId);

    @Query("SELECT * FROM Journal WHERE journalId = :id LIMIT 1")
    LiveData<Journal> getJournalById(int id);

    // Corrected to get the single most recent journal for a plant
    @Query("SELECT * FROM Journal WHERE plantId = :plantId ORDER BY dateCreated DESC LIMIT 1")
    LiveData<Journal> getLatestJournalByPlantId(int plantId);
}
