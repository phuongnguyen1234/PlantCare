package com.example.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.TaskScopeDao;
import com.example.plantcare.data.entity.TaskScope;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class TaskScopeRepository {
    private final TaskScopeDao taskScopeDao;
    private final ExecutorService executorService;

    public TaskScopeRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskScopeDao = db.taskScopeDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public void insert(TaskScope taskScope) {
        executorService.execute(() -> taskScopeDao.insert(taskScope));
    }

    public void deleteByTaskId(int taskId) {
        executorService.execute(() -> taskScopeDao.deleteByTaskId(taskId));
    }

    public LiveData<List<TaskScope>> getTaskScopesByPlantId(int plantId) {
        return taskScopeDao.getTaskScopesByPlantId(plantId);
    }

    public LiveData<List<TaskScope>> getPlantsByTaskId(int taskId) {
        return taskScopeDao.getPlantsByTaskId(taskId);
    }
}
