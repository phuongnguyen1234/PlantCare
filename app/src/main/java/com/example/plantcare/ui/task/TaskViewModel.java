package com.example.plantcare.ui.task;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.model.TaskWithPlants;
import com.example.plantcare.data.repository.HistoryRepository;
import com.example.plantcare.data.repository.PlantRepository;
import com.example.plantcare.data.repository.TaskRepository;
import com.example.plantcare.notification.TaskAlarmScheduler;
import com.example.plantcare.ui.base.BaseViewModel;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class TaskViewModel extends BaseViewModel {
    private final TaskRepository repository;
    private final PlantRepository plantRepository;
    private final HistoryRepository historyRepository;

    private final LiveData<List<TaskWithPlants>> allTasksWithPlantsSource;
    private final LiveData<List<TaskWithPlants>> allTasksWithPlants;

    private final MutableLiveData<Boolean> _navigateToAddTask = new MutableLiveData<>();
    public LiveData<Boolean> navigateToAddTask = _navigateToAddTask;

    private final MutableLiveData<Integer> _navigateToEditTask = new MutableLiveData<>();
    public LiveData<Integer> navigateToEditTask = _navigateToEditTask;

    private final Handler statusCheckerHandler = new Handler(Looper.getMainLooper());
    private Runnable statusCheckerRunnable;
    private static final long CHECK_INTERVAL_MILLIS = 60000; // 1 minute

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        plantRepository = new PlantRepository(application);
        historyRepository = new HistoryRepository(application);

        allTasksWithPlantsSource = repository.getAllTasksWithPlants();
        allTasksWithPlantsSource.observeForever(this::cleanupOrphanTasks);

        allTasksWithPlants = Transformations.map(allTasksWithPlantsSource, tasks ->
                tasks.stream()
                        .filter(taskWithPlants -> taskWithPlants.plants != null && !taskWithPlants.plants.isEmpty())
                        .collect(Collectors.toList()));

        startStatusChecker();
    }

    private void startStatusChecker() {
        statusCheckerRunnable = new Runnable() {
            @Override
            public void run() {
                List<TaskWithPlants> currentTasks = allTasksWithPlantsSource.getValue();
                if (currentTasks != null) {
                    LocalDateTime now = LocalDateTime.now();
                    for (TaskWithPlants twp : currentTasks) {
                        if (twp.task != null && (twp.task.getStatus() == Status.SCHEDULED || twp.task.getStatus() == Status.READY) &&
                            twp.task.getExpiration() != null && twp.task.getExpiration().isBefore(now)) {
                            processTask(twp, false);
                        }
                    }
                }
                statusCheckerHandler.postDelayed(this, CHECK_INTERVAL_MILLIS);
            }
        };
        statusCheckerHandler.post(statusCheckerRunnable);
    }

    private void cleanupOrphanTasks(List<TaskWithPlants> tasks) {
        if (tasks == null) return;
        List<Task> orphansToDelete = tasks.stream()
                .filter(taskWithPlants -> taskWithPlants.plants == null || taskWithPlants.plants.isEmpty())
                .map(taskWithPlants -> taskWithPlants.task)
                .collect(Collectors.toList());

        if (!orphansToDelete.isEmpty()) {
            TaskRepository.databaseWriteExecutor.execute(() -> {
                for (Task orphan : orphansToDelete) {
                    TaskAlarmScheduler.cancel(getApplication(), orphan.getTaskId());
                    repository.delete(orphan);
                }
                _toastMessage.postValue("Đã tự động xóa các công việc không hợp lệ.");
            });
        }
    }

    public LiveData<List<TaskWithPlants>> getAllTasksWithPlants() {
        return allTasksWithPlants;
    }

    public void deleteTask(Task task) {
        TaskRepository.databaseWriteExecutor.execute(() -> {
            TaskAlarmScheduler.cancel(getApplication(), task.getTaskId());
            repository.delete(task);
            _toastMessage.postValue("Đã xóa công việc");
            repository.triggerRefresh();
        });
    }

    public void processTask(TaskWithPlants taskWithPlants, boolean isCompleted) {
        if (taskWithPlants == null || taskWithPlants.task == null) return;

        if (isCompleted) {
            if (taskWithPlants.task.getExpiration() != null && taskWithPlants.task.getExpiration().isBefore(LocalDateTime.now())) {
                _toastMessage.postValue("Không thể hoàn thành công việc đã hết hạn.");
                return;
            }
        }

        TaskRepository.databaseWriteExecutor.execute(() -> {
            Context context = getApplication().getApplicationContext();
            Task task = taskWithPlants.task;
            
            String plantNames = "";
            if(taskWithPlants.plants != null && !taskWithPlants.plants.isEmpty()){
                 plantNames = taskWithPlants.plants.stream().map(Plant::getName).collect(Collectors.joining(", "));
            }

            History history = new History();
            history.setTaskName(task.getName());
            history.setTaskType(task.getType());
            history.setStatus(isCompleted ? Status.COMPLETED : Status.MISSED);
            
            if (isCompleted) {
                if (plantNames.isEmpty()) return; 
                _toastMessage.postValue("Đã hoàn thành " + task.getName());
                history.setContent("Hoàn thành công việc: " + task.getName() + " cho " + plantNames);
            } else { 
                 history.setContent("Bỏ lỡ công việc: " + task.getName() + " cho " + plantNames);
            }
            history.setNotifyTime(task.getNotifyTime());
            history.setDateCompleted((isCompleted? LocalDateTime.now() : null));
            historyRepository.insert(history);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.cancel(task.getTaskId());
            }

            Integer freq = task.getFrequency();
            if (!task.isRepeat() || freq == null || freq <= 0) {
                TaskAlarmScheduler.cancel(context, task.getTaskId());
                repository.delete(task);
            } else {
                Task taskToUpdate = new Task();
                taskToUpdate.setTaskId(task.getTaskId());
                taskToUpdate.setName(task.getName());
                taskToUpdate.setType(task.getType());
                taskToUpdate.setRepeat(task.isRepeat());
                taskToUpdate.setFrequency(task.getFrequency());
                taskToUpdate.setFrequencyUnit(task.getFrequencyUnit());
                taskToUpdate.setNotifyStart(task.getNotifyStart());
                taskToUpdate.setNotifyEnd(task.getNotifyEnd());
                taskToUpdate.setNote(task.getNote());
                taskToUpdate.setNotifyTime(task.getNotifyTime());
                taskToUpdate.setExpiration(task.getExpiration());
                taskToUpdate.setStatus(task.getStatus());

                FrequencyUnit freqUnit = taskToUpdate.getFrequencyUnit();
                LocalDateTime now = LocalDateTime.now();
                ChronoUnit unit;
                if (freqUnit == null) {
                    unit = ChronoUnit.DAYS;
                } else {
                    switch (freqUnit) {
                        case HOUR: unit = ChronoUnit.HOURS; break;
                        case DAY: unit = ChronoUnit.DAYS; break;
                        case WEEK: unit = ChronoUnit.WEEKS; break;
                        case MONTH: unit = ChronoUnit.MONTHS; break;
                        case YEAR: unit = ChronoUnit.YEARS; break;
                        default: unit = ChronoUnit.DAYS;
                    }
                }

                LocalDateTime nextNotifyTime = now.plus(freq, unit);
                taskToUpdate.setNotifyTime(nextNotifyTime);
                taskToUpdate.setExpiration(nextNotifyTime.plusHours(1));
                taskToUpdate.setStatus(Status.SCHEDULED);
                
                repository.update(taskToUpdate);
                TaskAlarmScheduler.schedule(context, taskToUpdate);
            }
            repository.triggerRefresh();
        });
    }

    public static void processTaskStatic(Context context, Task task, boolean isCompleted) {
        Application app = null;
        Context appCtx = context.getApplicationContext();
        if (appCtx instanceof Application) {
            app = (Application) appCtx;
        }

        TaskRepository repository;
        HistoryRepository historyRepository;

        if (app != null) {
            repository = new TaskRepository(app);
            historyRepository = new HistoryRepository(app);
        } else {
            repository = new TaskRepository(context);
            historyRepository = new HistoryRepository(context);
        }

        History history = new History();
        history.setTaskName(task.getName());
        history.setTaskType(task.getType());
        history.setStatus(isCompleted ? Status.COMPLETED : Status.MISSED);
        history.setContent("Đã " + (isCompleted ? "hoàn thành" : "bỏ lỡ") + " " + task.getName());
        history.setNotifyTime(task.getNotifyTime());
        history.setDateCompleted((isCompleted? LocalDateTime.now() : null));
        historyRepository.insert(history);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(task.getTaskId());

        Integer freq = task.getFrequency();
        if (!task.isRepeat() || freq == null || freq <= 0) {
            TaskAlarmScheduler.cancel(context, task.getTaskId());
            repository.delete(task);
        } else {
            Task taskToUpdate = new Task();
            taskToUpdate.setTaskId(task.getTaskId());
            taskToUpdate.setName(task.getName());
            taskToUpdate.setType(task.getType());
            taskToUpdate.setRepeat(task.isRepeat());
            taskToUpdate.setFrequency(task.getFrequency());
            taskToUpdate.setFrequencyUnit(task.getFrequencyUnit());
            taskToUpdate.setNotifyStart(task.getNotifyStart());
            taskToUpdate.setNotifyEnd(task.getNotifyEnd());
            taskToUpdate.setNote(task.getNote());
            taskToUpdate.setNotifyTime(task.getNotifyTime());
            taskToUpdate.setExpiration(task.getExpiration());
            taskToUpdate.setStatus(task.getStatus());

            FrequencyUnit freqUnit = taskToUpdate.getFrequencyUnit();
            LocalDateTime now = LocalDateTime.now();
            ChronoUnit unit;
            switch (freqUnit) {
                case DAY:
                    unit = ChronoUnit.DAYS;
                    break;
                case HOUR:
                    unit = ChronoUnit.HOURS;
                    break;
                case WEEK:
                    unit = ChronoUnit.WEEKS;
                    break;
                case MONTH:
                    unit = ChronoUnit.MONTHS;
                    break;
                case YEAR:
                    unit = ChronoUnit.YEARS;
                    break;
                default:
                    unit = ChronoUnit.DAYS;
            }
            LocalDateTime nextNotifyTime = now.plus(freq, unit);
            taskToUpdate.setNotifyTime(nextNotifyTime);
            taskToUpdate.setExpiration(nextNotifyTime.plusHours(1));
            taskToUpdate.setStatus(Status.SCHEDULED);
            repository.update(taskToUpdate);

            TaskAlarmScheduler.schedule(context, taskToUpdate);
        }
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

    public LiveData<List<Plant>> getAllPlants() {
        return plantRepository.getAllPlants();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        allTasksWithPlantsSource.removeObserver(this::cleanupOrphanTasks);
        if (statusCheckerHandler != null && statusCheckerRunnable != null) {
            statusCheckerHandler.removeCallbacks(statusCheckerRunnable);
        }
    }
}
