package com.example.plantcare.data.repository;

import com.example.plantcare.MainApplication;
import com.example.plantcare.data.dao.JournalImageDao;
import com.example.plantcare.data.entity.JournalImage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalImageRepository {
    private final JournalImageDao journalImageDao;
    private final ExecutorService executorService;

    public JournalImageRepository() {
        journalImageDao = MainApplication.database.journalImageDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(JournalImage image) {
        executorService.execute(() -> journalImageDao.insert(image));
    }

    public void update(JournalImage image) {
        executorService.execute(() -> journalImageDao.update(image));
    }

    public void delete(JournalImage image) {
        executorService.execute(() -> journalImageDao.delete(image));
    }

    public List<JournalImage> getImagesByJournal(int journalId) {
        return journalImageDao.getImagesByJournal(journalId);
    }
}
