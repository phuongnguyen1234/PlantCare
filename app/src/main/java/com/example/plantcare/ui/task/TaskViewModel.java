package com.example.plantcare.ui.task;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.data.repository.TaskRepository;
import com.example.plantcare.notification.TaskAlarmScheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public void processTask(Task task, boolean isCompleted) {
        if (task == null) return;

        // Lưu snapshot vào History
        History history = new History();
        history.setTaskName(task.getName());
        history.setTaskType(task.getType());
        history.setStatus(isCompleted ? Status.COMPLETED : Status.MISSED);
        history.setContent((isCompleted ? "Hoàn thành" : "Bỏ lỡ") + " công việc: " + task.getName());
        history.setNotifyTime(task.getNotifyTime());
        history.setDateCompleted(LocalDateTime.now());
        repository.insertHistory(history);

        Integer freq = task.getFrequency();
        FrequencyUnit freqUnit = task.getFrequencyUnit();
        LocalDateTime now = LocalDateTime.now();

        if (freq == null || freq <= 0) {
            // Không lặp → xóa task
            repository.delete(task);
        } else {
            // Map FrequencyUnit sang ChronoUnit đầy đủ
            ChronoUnit unit;
            if (freqUnit == null) unit = ChronoUnit.DAYS; // fallback
            else switch (freqUnit) {
                case HOUR: unit = ChronoUnit.HOURS; break;
                case DAY: unit = ChronoUnit.DAYS; break;
                case WEEK: unit = ChronoUnit.WEEKS; break;
                case MONTH: unit = ChronoUnit.MONTHS; break;
                case YEAR: unit = ChronoUnit.YEARS; break;
                default: unit = ChronoUnit.DAYS; // fallback
            }

            // Task lặp → tính notifyTime tiếp theo từ thời điểm hiện tại
            LocalDateTime nextNotifyTime = now.plus(freq, unit);
            task.setNotifyTime(nextNotifyTime);
            task.setStatus(Status.SCHEDULED); // reset trạng thái
            repository.update(task);

            // Reschedule alarm
            TaskAlarmScheduler.schedule(getApplication(), task);
        }
    }

    public static void processTaskStatic(Context context, Task task, boolean isCompleted) {
        TaskRepository repository = new TaskRepository((Application) context.getApplicationContext());

        Status newStatus = isCompleted ? Status.COMPLETED : Status.MISSED;

        // Lưu vào History
        History history = new History();
        history.setTaskName(task.getName());
        history.setTaskType(task.getType());
        history.setStatus(newStatus);
        history.setContent((isCompleted ? "Hoàn thành" : "Bỏ lỡ") + " công việc: " + task.getName());
        history.setNotifyTime(task.getNotifyTime());
        history.setDateCompleted(LocalDateTime.now());
        repository.insertHistory(history);

        Integer freq = task.getFrequency();
        FrequencyUnit freqUnit = task.getFrequencyUnit();
        LocalDateTime now = LocalDateTime.now();

        if (freq == null || freq <= 0) {
            // Không lặp → xóa task
            repository.delete(task);
        } else {
            // Task lặp → tính notifyTime tiếp theo từ thời điểm hiện tại
            ChronoUnit unit;
            switch (freqUnit) {
                case DAY: unit = ChronoUnit.DAYS; break;
                case HOUR: unit = ChronoUnit.HOURS; break;
                case WEEK: unit = ChronoUnit.WEEKS; break;
                case MONTH: unit = ChronoUnit.MONTHS; break;
                case YEAR: unit = ChronoUnit.YEARS; break;
                default: unit = ChronoUnit.DAYS;
            }
            LocalDateTime nextNotifyTime = now.plus(freq, unit);
            task.setNotifyTime(nextNotifyTime);
            task.setStatus(Status.SCHEDULED);
            repository.update(task);

            // Reschedule alarm
            TaskAlarmScheduler.schedule(context, task);
        }
    }

}