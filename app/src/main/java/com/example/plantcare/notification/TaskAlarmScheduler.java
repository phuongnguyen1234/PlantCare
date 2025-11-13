package com.example.plantcare.notification;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import com.example.plantcare.data.entity.Task;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TaskAlarmScheduler {

    public static void schedule(Context context, Task task) {
        if (task.getNotifyTime() == null) return;

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Android 12+ cần kiểm tra quyền exact alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Dẫn user vào Settings để bật quyền exact alarm
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // bắt buộc nếu gọi từ BroadcastReceiver
                context.startActivity(intent);
                return; // không đặt alarm nếu chưa có quyền
            }
        }

        long triggerAtMillis = task.getNotifyTime()
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        intent.putExtra("taskId", task.getTaskId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                task.getTaskId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }


    public static void cancel(Context context, int taskId) {
        Intent intent = new Intent(context, TaskAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    // Gọi khi task là loại lặp — lên lịch lần kế tiếp
    public static void scheduleNext(Context context, Task task) {
        if (task.getFrequency() <= 0 || task.getFrequencyUnit() == null) return;

        LocalDateTime nextTime = DateTimeUtils.addFrequency(
                task.getNotifyTime(),
                task.getFrequency(),
                task.getFrequencyUnit()
        );
        task.setNotifyTime(nextTime);
        schedule(context, task);
    }
}
