package com.example.plantcare.ui.task;

import android.app.Application;
import android.app.NotificationManager;
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
import com.example.plantcare.data.repository.HistoryRepository;
import com.example.plantcare.data.repository.TaskRepository;
import com.example.plantcare.notification.TaskAlarmScheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TaskViewModel extends AndroidViewModel {
    private final TaskRepository repository;

    private final HistoryRepository historyRepository;

    private final LiveData<List<TaskWithPlants>> allTasksWithPlants;

    private final MutableLiveData<Boolean> _navigateToAddTask = new MutableLiveData<>();
    public LiveData<Boolean> navigateToAddTask = _navigateToAddTask;

    private final MutableLiveData<Integer> _navigateToEditTask = new MutableLiveData<>();
    public LiveData<Integer> navigateToEditTask = _navigateToEditTask;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
        historyRepository = new HistoryRepository(application);
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
        // Hủy bỏ alarm trước khi xóa task khỏi DB
        TaskAlarmScheduler.cancel(getApplication(), task.getTaskId());
        repository.delete(task);
        repository.triggerRefresh();
    }

    /** Cho TaskFragment gọi khi nhận Broadcast từ notification */
    public void reloadTasks() {
        // Vì LiveData lấy từ Room → chỉ cần gọi repo để kích LiveData update
        repository.triggerRefresh();
    }

    public void processTask(Task task, boolean isCompleted) {
        if (task == null) return;

        TaskRepository.databaseWriteExecutor.execute(() -> {
            Context context = getApplication().getApplicationContext();

            // 1. GHI VÀO HISTORY VỚI DỮ LIỆU GỐC (CHƯA BỊ SỬA)
            History history = new History();
            history.setTaskName(task.getName());
            history.setTaskType(task.getType());
            history.setStatus(isCompleted ? Status.COMPLETED : Status.MISSED);
            history.setContent((isCompleted ? "Hoàn thành" : "Bỏ lỡ") + " công việc: " + task.getName());
            history.setNotifyTime(task.getNotifyTime()); // QUAN TRỌNG: Lấy notifyTime gốc
            history.setDateCompleted(LocalDateTime.now());
            historyRepository.insert(history);

            // 2. Hủy thông báo nếu nó đang hiển thị
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.cancel(task.getTaskId());
            }

            // 3. XỬ LÝ TASK: Xóa hoặc cập nhật (Bây giờ mới bắt đầu sửa đổi task)
            Integer freq = task.getFrequency();
            if (!task.isRepeat() || freq == null || freq <= 0) {
                // Không lặp lại -> Xóa Task
                TaskAlarmScheduler.cancel(context, task.getTaskId());
                repository.delete(task); // Repository đã được sửa để chạy đồng bộ
            } else {
                // Lặp lại -> Cập nhật Task
                // TẠO BẢN SAO của task để không sửa đổi instance trong adapter
                Task taskToUpdate = new Task();
                taskToUpdate.setTaskId(task.getTaskId());
                taskToUpdate.setName(task.getName());
                taskToUpdate.setType(task.getType());
                taskToUpdate.setFrequency(task.getFrequency());
                taskToUpdate.setFrequencyUnit(task.getFrequencyUnit());
                taskToUpdate.setRepeat(task.isRepeat());
                taskToUpdate.setNotifyTime(task.getNotifyTime()); // QUAN TRỌNG: Lấy notifyTime gốc
                taskToUpdate.setExpiration(task.getExpiration());
                taskToUpdate.setStatus(task.getStatus());
                taskToUpdate.setNote(task.getNote());
                taskToUpdate.setNotifyStart(task.getNotifyStart());
                taskToUpdate.setNotifyEnd(task.getNotifyEnd());


                FrequencyUnit freqUnit = taskToUpdate.getFrequencyUnit();
                LocalDateTime now = LocalDateTime.now();
                ChronoUnit unit;
                if (freqUnit == null) {
                    unit = ChronoUnit.DAYS; // fallback
                } else {
                    switch (freqUnit) {
                        case HOUR: unit = ChronoUnit.HOURS; break;
                        case WEEK: unit = ChronoUnit.WEEKS; break;
                        case MONTH: unit = ChronoUnit.MONTHS; break;
                        case YEAR: unit = ChronoUnit.YEARS; break;
                        default: unit = ChronoUnit.DAYS;
                    }
                }

                LocalDateTime nextNotifyTime = now.plus(freq, unit);
                taskToUpdate.setNotifyTime(nextNotifyTime); // Sửa notifyTime
                taskToUpdate.setExpiration(nextNotifyTime.plusHours(1)); // Sửa expiration
                taskToUpdate.setStatus(Status.SCHEDULED); // Reset trạng thái
                repository.update(taskToUpdate); // Gọi hàm update đồng bộ bên trong luồng nền này

                // Lên lịch lại cho lần tiếp theo
                TaskAlarmScheduler.schedule(context, taskToUpdate);
            }
            repository.triggerRefresh();
        });
    }

    public static void processTaskStatic(Context context, Task task, boolean isCompleted) {
        // Tạo TaskRepository an toàn từ context - dùng Application nếu có
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
            // fallback: tạo repository dùng AppDatabase trực tiếp (thêm constructor mới bên repository)
            repository = new TaskRepository(context);
            historyRepository = new HistoryRepository(context);
        }

        Status newStatus = isCompleted ? Status.COMPLETED : Status.MISSED;

        // Lưu vào History
        History history = new History();
        history.setTaskName(task.getName());
        history.setTaskType(task.getType());
        history.setStatus(isCompleted ? Status.COMPLETED : Status.MISSED);
        history.setContent((isCompleted ? "Hoàn thành" : "Bỏ lỡ") + " công việc: " + task.getName());
        history.setNotifyTime(task.getNotifyTime());
        history.setDateCompleted(LocalDateTime.now());
        historyRepository.insert(history);

        // Hủy bỏ thông báo (nếu có)
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(task.getTaskId());

        Integer freq = task.getFrequency();
        // Kiểm tra task có lặp lại không
        if (!task.isRepeat() || freq == null || freq <= 0) {
            // Không lặp → xóa task
            // Hủy alarm trước khi xóa
            TaskAlarmScheduler.cancel(context, task.getTaskId());
            repository.delete(task);
        } else {
            // Task lặp → tính notifyTime tiếp theo
            FrequencyUnit freqUnit = task.getFrequencyUnit();
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
            task.setNotifyTime(nextNotifyTime);
            task.setExpiration(nextNotifyTime.plusHours(1));
            task.setStatus(Status.SCHEDULED); // Reset trạng thái
            repository.update(task);

            // Reschedule alarm cho lần tiếp theo
            TaskAlarmScheduler.schedule(context, task);
        }
    }
}
