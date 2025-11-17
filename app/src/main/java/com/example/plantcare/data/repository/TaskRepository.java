package com.example.plantcare.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

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
    public static ExecutorService databaseWriteExecutor = AppDatabase.databaseWriteExecutor;
    // LiveData chính
    private final LiveData<List<TaskWithPlants>> allTasksWithPlants;

    // Trigger để ép reload
    private final MutableLiveData<Boolean> refreshTrigger = new MutableLiveData<>();
    private final MediatorLiveData<List<TaskWithPlants>> mediator = new MediatorLiveData<>();


    public TaskRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        historyDao = db.historyDao();
        executorService = AppDatabase.databaseWriteExecutor;

        // Lấy LiveData gốc từ DAO
        LiveData<List<TaskWithPlants>> source = taskDao.getAllTasksWithPlants();
        mediator.addSource(source, mediator::setValue);

        // Giữ lại trigger để có thể gọi từ bên ngoài
        mediator.addSource(refreshTrigger, v -> {
            // Lấy lại giá trị mới nhất từ nguồn LiveData gốc
            // Thao tác này sẽ kích hoạt lại việc gửi dữ liệu đến observers
            mediator.setValue(source.getValue());
        });
        allTasksWithPlants = mediator;
    }

    // NEW constructor: accept Context (fallback)
    public TaskRepository(Context context) {
        AppDatabase db = AppDatabase.getDatabase(context.getApplicationContext());
        taskDao = db.taskDao();
        historyDao = db.historyDao();
        executorService = AppDatabase.databaseWriteExecutor;

        LiveData<List<TaskWithPlants>> source = taskDao.getAllTasksWithPlants();
        mediator.addSource(source, mediator::setValue);

        mediator.addSource(refreshTrigger, v -> {
            mediator.setValue(source.getValue());
        });
        allTasksWithPlants = mediator;
    }

    /** Gọi để refresh LiveData */
    public void triggerRefresh() {
        refreshTrigger.postValue(true);
    }

    public LiveData<List<TaskWithPlants>> getAllTasksWithPlants() {
        return allTasksWithPlants;
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

    public LiveData<Task> getTaskById(int taskId) {
        return taskDao.getTaskById(taskId);
    }


}
