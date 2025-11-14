package com.example.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.model.TaskWithPlants;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class TaskRepository {
    private final TaskDao taskDao;
    private final HistoryDao historyDao;
    private final ExecutorService executorService;

    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        historyDao = db.historyDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public long insertAndGetId(Task task) {
        return taskDao.insert(task);
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public LiveData<List<TaskWithPlants>> getAllTasksWithPlants() {
        return taskDao.getAllTasksWithPlants();
    }

    public LiveData<Task> getTaskById(int taskId) {
        return taskDao.getTaskById(taskId);
    }

    public void insertHistory(History history) {
        executorService.execute(() -> historyDao.insert(history));
    }

}