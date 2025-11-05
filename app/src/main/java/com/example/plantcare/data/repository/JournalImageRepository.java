package com.example.plantcare.data.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalImageDao;
import com.example.plantcare.data.entity.JournalImage;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JournalImageRepository {
    private final JournalImageDao journalImageDao;
    private final ExecutorService executorService;

    public JournalImageRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "plantcare.db").build();
        journalImageDao = db.journalImageDao();
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
