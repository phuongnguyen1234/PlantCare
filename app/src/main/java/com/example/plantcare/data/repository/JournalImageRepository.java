package com.example.plantcare.data.repository;

import android.app.Application;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalImageDao;
import com.example.plantcare.data.entity.JournalImage;

import java.util.concurrent.ExecutorService;

public class JournalImageRepository {
    private final JournalImageDao journalImageDao;
    private final ExecutorService executorService;

    public JournalImageRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalImageDao = db.journalImageDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public void insert(JournalImage journalImage) {
        executorService.execute(() -> journalImageDao.insert(journalImage));
    }
}
