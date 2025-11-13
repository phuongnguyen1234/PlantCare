package com.example.plantcare.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;

import java.util.concurrent.Executors;

public class TaskActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("COMPLETE_TASK".equals(intent.getAction())) {
            int taskId = intent.getIntExtra("taskId", -1);
            if (taskId == -1) return;

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getDatabase(context);
                Task task = db.taskDao().getTaskSync(taskId);
                if (task != null) {
                    task.setStatus(Status.COMPLETED);
                    db.taskDao().update(task);
                }

                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                nm.cancel(taskId);
            });
        }
    }
}
