package com.example.plantcare.data.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.TaskScopeDao;
import com.example.plantcare.data.entity.TaskScope;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskScopeRepository {
    private final TaskScopeDao taskScopeDao;
    private final ExecutorService executorService;

    public TaskScopeRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "plantcare.db").build();
        taskScopeDao = db.taskScopeDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(TaskScope scope) {
        executorService.execute(() -> taskScopeDao.insert(scope));
    }

    public void delete(int plantId, int taskId) {
        executorService.execute(() -> taskScopeDao.delete(plantId, taskId));
    }

    public List<TaskScope> getTasksByPlantId(int plantId) {
        return taskScopeDao.getTasksByPlantId(plantId);
    }

    public List<TaskScope> getPlantsByTaskId(int taskId) {
        return taskScopeDao.getPlantsByTaskId(taskId);
    }
}
