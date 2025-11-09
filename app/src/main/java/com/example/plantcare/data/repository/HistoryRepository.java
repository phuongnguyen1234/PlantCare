package com.example.plantcare.data.repository;

import androidx.lifecycle.LiveData;

import com.example.plantcare.MainApplication;
import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.entity.History;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryRepository {
    private final HistoryDao historyDao;
    private final ExecutorService executorService;

    public HistoryRepository() {
        historyDao = MainApplication.database.historyDao();
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

    public LiveData<List<History>> getAllHistory() {
        return historyDao.getAllHistory();
    }

    public LiveData<List<History>> getHistoryByStatus(String status) {
        return historyDao.getHistoryByStatus(status);
    }
}
