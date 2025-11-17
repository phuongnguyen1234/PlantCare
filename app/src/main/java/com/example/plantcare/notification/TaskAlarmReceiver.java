package com.example.plantcare.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;

import java.time.LocalTime;
import java.util.concurrent.Executors;

public class TaskAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int taskId = intent.getIntExtra("taskId", -1);
        Log.d("Alarm", "Received alarm for task " + taskId);
        if (taskId == -1) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            TaskDao dao = AppDatabase.getDatabase(context).taskDao();
            Task task = dao.getTaskSync(taskId);
            if (task == null) return;

            // Kiểm tra xem giờ hiện tại có nằm trong notifyStart–notifyEnd không
            LocalTime now = LocalTime.now();
            if (!DateTimeUtils.isWithinNotifyRange(task, now)) {
                return; // Ngoài phạm vi thông báo → không chuyển READY
            }

            // CHUYỂN TRẠNG THÁI → READY
            task.setStatus(Status.READY);
            dao.update(task);

            // Hiển thị notification
            NotificationHelper.showTaskNotification(context, task);

            // Lên lịch alarm expiration (notifyTime + 1h)
            DateTimeUtils.scheduleExpirationAutoCancel(context, task);
        });
    }
}
