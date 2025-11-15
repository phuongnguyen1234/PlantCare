package com.example.plantcare.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.FrequencyUnit;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public class DateTimeUtils {

    public static boolean isWithinNotifyRange(Task task, LocalTime now) {
        if (task.getNotifyStart() == null || task.getNotifyEnd() == null) return true;
        LocalTime start = task.getNotifyStart().toLocalTime();
        LocalTime end = task.getNotifyEnd().toLocalTime();
        return !now.isBefore(start) && !now.isAfter(end);
    }

    public static LocalDateTime addFrequency(LocalDateTime base, int freq, FrequencyUnit unit) {
        switch (unit) {
            case HOUR: return base.plusHours(freq);
            case DAY: return base.plusDays(freq);
            case WEEK: return base.plusWeeks(freq);
            default: return base;
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleExpirationAutoCancel(Context context, Task task) {
        if (task.getExpiration() == null) return;

        long triggerAtMillis = task.getExpiration()
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();

        Intent intent = new Intent(context, TaskActionReceiver.class);
        intent.setAction("EXPIRE_TASK");
        intent.putExtra("taskId", task.getTaskId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, task.getTaskId(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }
}
