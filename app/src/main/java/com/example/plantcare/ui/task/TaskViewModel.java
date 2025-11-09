package com.example.plantcare.ui.task;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.repository.TaskRepository;

import java.util.List;

public class TaskViewModel extends ViewModel {
    private final TaskRepository repository;
    private final LiveData<List<Task>> allTasks;

    private final MutableLiveData<Boolean> _navigateToTaskDetail = new MutableLiveData<>();
    public LiveData<Boolean> navigateToTaskDetail = _navigateToTaskDetail;

    public TaskViewModel() {
        repository = new TaskRepository();
        allTasks = repository.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void onFabClicked() {
        _navigateToTaskDetail.setValue(true);
    }

    public void onNavigatedToTaskDetail() {
        _navigateToTaskDetail.setValue(false);
    }
}