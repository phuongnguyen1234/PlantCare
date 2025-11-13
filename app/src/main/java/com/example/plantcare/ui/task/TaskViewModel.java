package com.example.plantcare.ui.task;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.data.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;
    private final LiveData<List<TaskWithPlants>> allTasksWithPlants;

    private final MutableLiveData<Boolean> _navigateToAddTask = new MutableLiveData<>();
    public LiveData<Boolean> navigateToAddTask = _navigateToAddTask;

    private final MutableLiveData<Integer> _navigateToEditTask = new MutableLiveData<>();
    public LiveData<Integer> navigateToEditTask = _navigateToEditTask;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        allTasksWithPlants = repository.getAllTasksWithPlants();
    }

    public LiveData<List<TaskWithPlants>> getAllTasksWithPlants() {
        return allTasksWithPlants;
    }

    public void onFabClicked() {
        _navigateToAddTask.setValue(true);
    }

    public void onNavigatedToAddTask() {
        _navigateToAddTask.setValue(false);
    }

    public void onEditTask(int taskId) {
        _navigateToEditTask.setValue(taskId);
    }

    public void onNavigatedToEditTask() {
        _navigateToEditTask.setValue(null);
    }

    public void deleteTask(Task task) {
        repository.delete(task);
    }

    public void completeTask(Task task) {
        if (task != null) {
            task.setStatus(Status.COMPLETED);
            repository.update(task);
        }
    }
}
