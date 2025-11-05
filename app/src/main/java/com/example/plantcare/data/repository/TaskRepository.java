package com.example.plantcare.data.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.entity.Task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskRepository {
    private final TaskDao taskDao;
    private final ExecutorService executorService;

    public TaskRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "plantcare.db").build();
        taskDao = db.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insert(Task task) {
        executorService.execute(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        executorService.execute(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        executorService.execute(() -> taskDao.delete(task));
    }

    public List<Task> getAllTasks() {
        return taskDao.getAllTasks();
    }

    public Task getTaskById(int id) {
        return taskDao.getTaskById(id);
    }

    public List<Task> getTasksByStatus(String status) {
        return taskDao.getTasksByStatus(status);
    }
}
