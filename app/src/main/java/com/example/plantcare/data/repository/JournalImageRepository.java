package com.example.plantcare.data.repository;

import android.app.Application;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalImageDao;
import com.example.plantcare.data.entity.JournalImage;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class JournalImageRepository {
    private final JournalImageDao journalImageDao;
    private final ExecutorService executorService;

    public interface Callback {
        void onResult(String imagePath);
    }

    public JournalImageRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        journalImageDao = db.journalImageDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public void insert(JournalImage journalImage) {
        executorService.execute(() -> journalImageDao.insert(journalImage));
    }

    public List<JournalImage> getImagesByJournalSync(int journalId) {
        return journalImageDao.getImagesByJournal(journalId);
    }
    public void getLatestImagePath(int journalId, Callback callback) {
        executorService.execute(() -> {
            List<JournalImage> images = journalImageDao.getImagesByJournal(journalId);
            String imagePath = null;
            if (images != null && !images.isEmpty()) {
                imagePath = images.get(images.size() - 1).getImageUrl();
            }
            if (callback != null) {
                callback.onResult(imagePath);
            }
        });
    }

}
