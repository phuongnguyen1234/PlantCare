package com.example.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.model.DailyTaskCount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class StatRepository {
    private final HistoryDao historyDao;

    private final PlantDao plantDao;

    private final TaskDao taskDao;

    public StatRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        historyDao = db.historyDao();
        plantDao = db.plantDao();
        taskDao = db.taskDao();
    }

    public LiveData<Integer> getTaskCount() {
        return taskDao.getActiveTaskCount();
    }

    public LiveData<Integer> getDueSoonTaskCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesFromNow = now.plusMinutes(30);
        return taskDao.getDueSoonTaskCount(now, thirtyMinutesFromNow);
    }

    public LiveData<Integer> getTodayCompletedTaskCount() {
        return historyDao.getTodayCompletedTaskCount();
    }

    public LiveData<List<DailyTaskCount>> getDailyCompletedTaskCounts() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return historyDao.getDailyCompletedTaskCounts(sevenDaysAgo);
    }

    public LiveData<Integer> getPlantCount() {
        return plantDao.getPlantCount();
    }
}
