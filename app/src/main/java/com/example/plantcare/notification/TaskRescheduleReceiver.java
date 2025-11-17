package com.example.plantcare.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.ui.task.TaskViewModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskRescheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Chỉ thực hiện khi nhận được Intent khởi động từ hệ thống
        final String action = intent.getAction();
        if (action == null || !action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }

        Log.d("RescheduleReceiver", "Boot completed. Rescheduling alarms and cleaning up tasks...");

        Executors.newSingleThreadExecutor().execute(() -> {
            List<Task> tasks = AppDatabase.getDatabase(context)
                    .taskDao()
                    .getAllTasksSync();

            LocalDateTime now = LocalDateTime.now();

            for (Task task : tasks) {
                // 1. Lên lịch lại các báo thức trong tương lai
                // Chỉ lên lịch lại cho các công việc còn hoạt động và có thời gian trong tương lai.
                if (task.getStatus().isActive() && task.getNotifyTime().isAfter(now)) {
                    TaskAlarmScheduler.schedule(context, task);
                }

                // 2. Xử lý các công việc đã quá hạn (dựa trên expiration)
                // Điều kiện này an toàn hơn nhiều, nó chỉ đúng khi công việc đã hết hạn thực sự.
                if (task.getExpiration() != null && task.getExpiration().isBefore(now)
                        && task.getStatus() == Status.READY) { // Chỉ xử lý các công việc đang ở trạng thái "Sẵn sàng"
                    Log.d("RescheduleReceiver", "Task " + task.getTaskId() + " is expired. Processing as MISSED.");
                    // Gọi hàm tĩnh processTask với isCompleted = false (bỏ lỡ)
                    TaskViewModel.processTaskStatic(context, task, false);
                }
            }
        });
    }
}

