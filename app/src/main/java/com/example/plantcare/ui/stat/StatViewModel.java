package com.example.plantcare.ui.stat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.model.DailyTaskCount;
import com.example.plantcare.data.repository.StatRepository;

import java.util.List;

public class StatViewModel extends AndroidViewModel {
    private final StatRepository statRepository;

    private final LiveData<Integer> plantCount;
    private final LiveData<Integer> taskCount;
    private final LiveData<Integer> dueSoonTaskCount;
    private final LiveData<Integer> todayCompletedTaskCount;
    private final LiveData<List<DailyTaskCount>> dailyCompletedTaskCounts;

    public StatViewModel(@NonNull Application application) {
        super(application);
        statRepository = new StatRepository(application);
        plantCount = statRepository.getPlantCount();
        taskCount = statRepository.getTaskCount();
        dueSoonTaskCount = statRepository.getDueSoonTaskCount();
        todayCompletedTaskCount = statRepository.getTodayCompletedTaskCount();
        dailyCompletedTaskCounts = statRepository.getDailyCompletedTaskCounts();
    }

    public LiveData<Integer> getPlantCount() {
        return plantCount;
    }

    public LiveData<Integer> getTaskCount() {
        return taskCount;
    }

    public LiveData<Integer> getDueSoonTaskCount() {
        return dueSoonTaskCount;
    }

    public LiveData<Integer> getTodayCompletedTaskCount() {
        return todayCompletedTaskCount;
    }

    public LiveData<List<DailyTaskCount>> getDailyCompletedTaskCounts() {
        return dailyCompletedTaskCounts;
    }
}
