package com.example.plantcare.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.enums.TaskType;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class HistoryRepository {
    private final HistoryDao historyDao;
    private final ExecutorService executorService;

    public HistoryRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        historyDao = db.historyDao();
        executorService = AppDatabase.databaseWriteExecutor;
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

    public LiveData<List<History>> getFilteredHistories(String taskType, List<String> statuses, String date, String searchQuery) {
        String query = (searchQuery == null || searchQuery.isEmpty()) ? null : "%" + searchQuery + "%";
        if (statuses == null || statuses.isEmpty()) {
            return historyDao.getFilteredHistoriesWithoutStatus(taskType, date, query);
        } else {
            return historyDao.getFilteredHistoriesWithStatus(taskType, statuses, date, query);
        }
    }

    public LiveData<List<History>> getHistoryByStatus(String status) {
        return historyDao.getHistoryByStatus(status);
    }
}
