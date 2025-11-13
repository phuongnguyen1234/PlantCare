package com.example.plantcare.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.entity.Task;

import java.time.LocalTime;
import java.util.concurrent.Executors;

public class TaskAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("taskId", -1);
        if (taskId == -1) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            Task task = AppDatabase.getDatabase(context).taskDao().getTaskSync(taskId);
            if (task == null) return;

            // Kiểm tra xem giờ hiện tại có nằm trong notifyStart–notifyEnd không
            LocalTime now = LocalTime.now();
            if (!DateTimeUtils.isWithinNotifyRange(task, now)) {
                return; // Ngoài phạm vi thông báo
            }

            // Tạo thông báo
            NotificationHelper.showTaskNotification(context, task);

            // Nếu là task lặp, lên lịch lần kế tiếp
            if (task.isRepeat()) {
                TaskAlarmScheduler.scheduleNext(context, task);
            }

            // Tự hủy nếu hết hạn
            DateTimeUtils.scheduleExpirationAutoCancel(context, task);
        });
    }
}
