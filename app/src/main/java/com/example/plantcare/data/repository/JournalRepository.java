package com.example.plantcare.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalDao;
import com.example.plantcare.data.entity.Journal;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class JournalRepository {
    private final JournalDao journalDao;
    private final ExecutorService executorService;

    public JournalRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalDao = db.journalDao();
        executorService = AppDatabase.databaseWriteExecutor;
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

    public LiveData<List<Journal>> getJournalsByPlantId(int plantId) {
        return journalDao.getJournalsByPlantId(plantId);
    }

    public LiveData<Journal> getJournalById(int journalId) {
        return journalDao.getJournalById(journalId);
    }
}
