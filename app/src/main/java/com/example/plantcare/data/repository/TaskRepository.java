package com.example.plantcare.data.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.dao.TaskScopeDao;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.entity.TaskScope;
import com.example.plantcare.data.model.TaskWithPlants;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class TaskRepository {
    private final AppDatabase db;
    private final TaskDao taskDao;
    private final TaskScopeDao taskScopeDao;
    private final HistoryDao historyDao;
    private final ExecutorService executorService;
    public static ExecutorService databaseWriteExecutor = AppDatabase.databaseWriteExecutor;

    private final LiveData<List<TaskWithPlants>> allTasksWithPlants;
    private final MutableLiveData<Boolean> refreshTrigger = new MutableLiveData<>();
    private final MediatorLiveData<List<TaskWithPlants>> mediator = new MediatorLiveData<>();

    public TaskRepository(Application application) {
        db = AppDatabase.getDatabase(application);
        taskDao = db.taskDao();
        taskScopeDao = db.taskScopeDao();
        historyDao = db.historyDao();
        executorService = AppDatabase.databaseWriteExecutor;

        LiveData<List<TaskWithPlants>> source = taskDao.getAllTasksWithPlants();
        mediator.addSource(source, mediator::setValue);
        mediator.addSource(refreshTrigger, v -> mediator.setValue(source.getValue()));
        allTasksWithPlants = mediator;
    }

    public TaskRepository(Context context) {
        db = AppDatabase.getDatabase(context.getApplicationContext());
        taskDao = db.taskDao();
        taskScopeDao = db.taskScopeDao();
        historyDao = db.historyDao();
        executorService = AppDatabase.databaseWriteExecutor;

        LiveData<List<TaskWithPlants>> source = taskDao.getAllTasksWithPlants();
        mediator.addSource(source, mediator::setValue);
        mediator.addSource(refreshTrigger, v -> mediator.setValue(source.getValue()));
        allTasksWithPlants = mediator;
    }

    public void triggerRefresh() {
        refreshTrigger.postValue(true);
    }

    public LiveData<List<TaskWithPlants>> getAllTasksWithPlants() {
        return allTasksWithPlants;
    }

    public long insertTaskWithScopes(Task task, Set<Integer> plantIds) {
        final long[] taskId = {-1L};
        db.runInTransaction(() -> {
            taskId[0] = taskDao.insert(task);
            if (taskId[0] == -1) {
                throw new RuntimeException("Failed to insert task.");
            }
            if (plantIds != null && !plantIds.isEmpty()) {
                for (Integer plantId : plantIds) {
                    taskScopeDao.insert(new TaskScope((int) taskId[0], plantId));
                }
            }
        });
        return taskId[0];
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

    public LiveData<Task> getTaskById(int taskId) {
        return taskDao.getTaskById(taskId);
    }
}
