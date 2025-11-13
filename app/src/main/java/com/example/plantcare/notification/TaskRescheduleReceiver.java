package com.example.plantcare.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.ui.task.TaskViewModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskRescheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> tasks = AppDatabase.getDatabase(context)
                    .taskDao()
                    .getAllTasksSync(); // lấy tất cả task

            LocalDateTime now = LocalDateTime.now();

            for (Task task : tasks) {
                if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                    // Reschedule alarms cho task lặp
                    if (task.isRepeat() && task.getStatus().isActive()) {
                        TaskAlarmScheduler.schedule(context, task);
                    }
                }

                // Kiểm tra task quá hạn → đánh dấu MISSED hoặc reschedule nếu lặp
                if (task.getStatus().isActive() && task.getNotifyTime().isBefore(now)) {
                    // gọi hàm tĩnh processTask
                    TaskViewModel.processTaskStatic(context, task, false);
                }
            }
        });
    }
}

