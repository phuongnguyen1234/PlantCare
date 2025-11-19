package com.example.plantcare;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.plantcare.notification.TaskCleanupWorker;

import java.util.concurrent.TimeUnit;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setupRecurringWork();
    }

    private void setupRecurringWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        PeriodicWorkRequest cleanupRequest = 
                new PeriodicWorkRequest.Builder(TaskCleanupWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "TaskCleanupWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                cleanupRequest
        );
    }
}
