package com.example.plantcare.data.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalDao;
import com.example.plantcare.data.entity.Journal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalRepository {
    private final JournalDao journalDao;
    private final ExecutorService executorService;

    public JournalRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "plantcare.db").build();
        journalDao = db.journalDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Journal journal) {
        executorService.execute(() -> journalDao.insert(journal));
    }

    public void update(Journal journal) {
        executorService.execute(() -> journalDao.update(journal));
    }

    public void delete(Journal journal) {
        executorService.execute(() -> journalDao.delete(journal));
    }

    public List<Journal> getJournalsByPlant(int plantId) {
        return journalDao.getJournalsByPlant(plantId);
    }

    public Journal getJournalById(int id) {
        return journalDao.getJournalById(id);
    }
}
