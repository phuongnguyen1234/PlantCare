package com.example.plantcare.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.data.model.JournalWithImages;

import java.util.List;

@Dao
public interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Journal journal);

    @Update
    void update(Journal journal);

    @Delete
    void delete(Journal journal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImages(List<JournalImage> images);

    @Query("SELECT * FROM JournalImage WHERE journalId = :journalId")
    List<JournalImage> getImagesByJournalId(int journalId);

    @Query("DELETE FROM JournalImage WHERE journalId = :journalId")
    void deleteImagesByJournalId(int journalId);

    @Query("DELETE FROM Journal WHERE plantId = :plantId")
    void deleteAllJournalsByPlantId(int plantId);

    @Query("DELETE FROM JournalImage WHERE journalId IN (SELECT journalId FROM Journal WHERE plantId = :plantId)")
    void deleteAllImagesForPlant(int plantId);

    @Query("SELECT i.* FROM JournalImage i INNER JOIN Journal j ON i.journalId = j.journalId WHERE j.plantId = :plantId")
    List<JournalImage> getAllImagesForPlantSync(int plantId);

    @Transaction
    @Query("SELECT * FROM Journal WHERE journalId = :id")
    LiveData<JournalWithImages> getJournalWithImagesById(int id);

    @Transaction
    @Query("SELECT j.* FROM Journal j " +
            "LEFT OUTER JOIN Journal j2 ON j.plantId = j2.plantId AND " +
            "    (j.dateCreated < j2.dateCreated OR (j.dateCreated = j2.dateCreated AND j.journalId < j2.journalId)) " +
            "WHERE j2.journalId IS NULL " +
            "ORDER BY j.dateCreated DESC, j.plantName ASC")
    LiveData<List<JournalWithImages>> getLatestJournalForEachPlant();

    @Transaction
    @Query("SELECT * FROM Journal WHERE plantId = :plantId ORDER BY dateCreated DESC")
    LiveData<List<JournalWithImages>> getJournalsWithImagesByPlantId(int plantId);
}
