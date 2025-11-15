package com.example.plantcare.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.plantcare.data.entity.JournalImage;

import java.util.List;

@Dao
public interface JournalImageDao {

    @Insert
    void insert(JournalImage image);

    @Update
    void update(JournalImage image);

    @Delete
    void delete(JournalImage image);

    @Query("SELECT * FROM JournalImage WHERE journalId = :journalId ORDER BY position ASC")
    List<JournalImage> getImagesByJournal(int journalId);

}
