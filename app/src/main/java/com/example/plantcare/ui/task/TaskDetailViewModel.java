package com.example.plantcare.ui.task;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.entity.TaskScope;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.data.repository.PlantRepository;
import com.example.plantcare.data.repository.TaskRepository;
import com.example.plantcare.data.repository.TaskScopeRepository;
import com.example.plantcare.notification.TaskAlarmScheduler;
import com.example.plantcare.ui.base.BaseViewModel;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskDetailViewModel extends BaseViewModel {

    private final TaskRepository taskRepository;
    private final PlantRepository plantRepository;
    private final TaskScopeRepository taskScopeRepository;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private final MutableLiveData<Boolean> _isEditMode = new MutableLiveData<>(false);
    public LiveData<Boolean> isEditMode = _isEditMode;

    // Form fields
    public final MutableLiveData<String> taskName = new MutableLiveData<>();
    public final MutableLiveData<TaskType> taskType = new MutableLiveData<>();
    public final MutableLiveData<String> notifyTime = new MutableLiveData<>();
    public final MutableLiveData<Boolean> isRepeat = new MutableLiveData<>(false);
    public final MutableLiveData<String> frequency = new MutableLiveData<>();
    public final MutableLiveData<FrequencyUnit> frequencyUnit = new MutableLiveData<>();
    public final MutableLiveData<String> notifyStart = new MutableLiveData<>();
    public final MutableLiveData<String> notifyEnd = new MutableLiveData<>();
    public final MutableLiveData<String> note = new MutableLiveData<>();
    public final MutableLiveData<Set<Integer>> selectedPlantIds = new MutableLiveData<>(new HashSet<>());

    public LiveData<List<Plant>> allPlants;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public TaskDetailViewModel(@NonNull Application application) {
        super(application);
        this.taskRepository = new TaskRepository(application);
        this.plantRepository = new PlantRepository(application);
        this.taskScopeRepository = new TaskScopeRepository(application);
        allPlants = plantRepository.getAllPlants();
    }

    public void start(int taskId) {
        if (taskId != -1) {
            _isEditMode.setValue(true);
            taskRepository.getTaskById(taskId).observeForever(this::populateFieldsFromTask);
            taskScopeRepository.getPlantsByTaskId(taskId).observeForever(this::updateSelectedPlants);
        } else {
            _isEditMode.setValue(false);
            resetFields();
        }
    }

    public void onAddClicked(Task task) {
        executor.execute(() -> {
            try {
                Set<Integer> plantIds = selectedPlantIds.getValue();
                long newTaskId = taskRepository.insertTaskWithScopes(task, plantIds);

                if (newTaskId != -1) {
                    task.setTaskId((int) newTaskId);
                    TaskAlarmScheduler.schedule(getApplication(), task);
                    _toastMessage.postValue("Thêm công việc thành công");
                } else {
                    _toastMessage.postValue("Thêm công việc thất bại");
                }
            } catch (Exception e) {
                _toastMessage.postValue("Lỗi khi thêm công việc: " + e.getMessage());
            }
            _navigateBack.postValue(true);
        });
    }


    public void onUpdateClicked(Task task) {
        executor.execute(() -> {
            taskRepository.update(task);
            TaskAlarmScheduler.schedule(getApplication(), task);

            Set<Integer> plantIds = selectedPlantIds.getValue();
            if (plantIds != null) {
                List<TaskScope> newScopes = plantIds.stream()
                        .map(pid -> new TaskScope(task.getTaskId(), pid))
                        .collect(Collectors.toList());
                taskScopeRepository.replaceAllByTaskId(task.getTaskId(), newScopes);
            }
            taskRepository.triggerRefresh();
            _toastMessage.postValue("Cập nhật công việc thành công");
            _navigateBack.postValue(true);
        });
    }

    private void resetFields() {
        taskName.setValue("");
        taskType.setValue(null);
        notifyTime.setValue("");
        isRepeat.setValue(false);
        frequency.setValue("");
        frequencyUnit.setValue(null);
        notifyStart.setValue("");
        notifyEnd.setValue("");
        note.setValue("");
        selectedPlantIds.setValue(new HashSet<>());
    }

    private void populateFieldsFromTask(Task task) {
        if (task == null) return;
        taskName.setValue(task.getName());
        taskType.setValue(task.getType());
        if (task.getNotifyTime() != null) notifyTime.setValue(task.getNotifyTime().format(dateTimeFormatter));
        isRepeat.setValue(task.isRepeat());
        frequency.setValue(String.valueOf(task.getFrequency()));
        frequencyUnit.setValue(task.getFrequencyUnit());
        if (task.getNotifyStart() != null) notifyStart.setValue(task.getNotifyStart().format(timeFormatter));
        if (task.getNotifyEnd() != null) notifyEnd.setValue(task.getNotifyEnd().format(timeFormatter));
        note.setValue(task.getNote());
    }

    private void updateSelectedPlants(List<TaskScope> scopes) {
        if (scopes == null) return;
        selectedPlantIds.setValue(scopes.stream().map(TaskScope::getPlantId).collect(Collectors.toSet()));
    }

    public void onIsRepeatChanged(boolean isChecked) {
        if (!isChecked) {
            frequency.setValue("");
            frequencyUnit.setValue(null);
            notifyStart.setValue("");
            notifyEnd.setValue("");
        }
    }
}
