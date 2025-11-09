package com.example.plantcare.data.repository;

import androidx.lifecycle.LiveData;

import com.example.plantcare.MainApplication;
import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.model.DailyTaskCount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StatRepository {
    private final HistoryDao historyDao;

    private final PlantDao plantDao;

    private final TaskDao taskDao;

    private final ExecutorService executorService;

    public StatRepository() {
        historyDao = MainApplication.database.historyDao();
        plantDao = MainApplication.database.plantDao();
        taskDao = MainApplication.database.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<Integer> getPlantCount() {
        return plantDao.getPlantCount();
    }

    public LiveData<Integer> getTaskCount() {
        return taskDao.getActiveTaskCount();
    }

    public LiveData<Integer> getDueSoonTaskCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysFromNow = now.plusDays(3);
        return taskDao.getDueSoonTaskCount(now, threeDaysFromNow);
    }

    public LiveData<Integer> getTodayCompletedTaskCount() {
        return historyDao.getTodayCompletedTaskCount();
    }

    public LiveData<List<DailyTaskCount>> getDailyCompletedTaskCounts() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return historyDao.getDailyCompletedTaskCounts(sevenDaysAgo);
    }
}
