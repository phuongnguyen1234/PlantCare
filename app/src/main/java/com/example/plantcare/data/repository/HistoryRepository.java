package com.example.plantcare.data.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.entity.History;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryRepository {
    private final HistoryDao historyDao;
    private final ExecutorService executorService;

    public HistoryRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "plantcare.db").build();
        historyDao = db.historyDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(History history) {
        executorService.execute(() -> historyDao.insert(history));
    }

    public void update(History history) {
        executorService.execute(() -> historyDao.update(history));
    }

    public void delete(History history) {
        executorService.execute(() -> historyDao.delete(history));
    }

    public List<History> getAllHistory() {
        return historyDao.getAllHistory();
    }

    public List<History> getHistoryByStatus(String status) {
        return historyDao.getHistoryByStatus(status);
    }
}
