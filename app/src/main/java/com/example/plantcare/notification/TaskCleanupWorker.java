package com.example.plantcare.notification;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;

import java.time.LocalDateTime;
import java.util.List;

public class TaskCleanupWorker extends Worker {
    public TaskCleanupWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        List<Task> tasks = db.taskDao().getAllTasksSync();

        for (Task task : tasks) {
            if (task.getExpiration() != null && task.getExpiration().isBefore(LocalDateTime.now())
                    && task.getStatus() == Status.SCHEDULED) {
                task.setStatus(Status.MISSED);
                db.taskDao().update(task);
            }
        }

        return Result.success();
    }
}
