package com.example.plantcare.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalDao;
import com.example.plantcare.data.entity.Journal;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class JournalRepository {
    private final JournalDao journalDao;
    private final ExecutorService executorService;

    public JournalRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalDao = db.journalDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public long insert(Journal journal) {
        Future<Long> future = executorService.submit(new Callable<Long>() {
            @Override
            public Long call() {
                return journalDao.insert(journal);
            }
        });
        try {
            return future.get(); // Đợi kết quả
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
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
