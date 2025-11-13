package com.example.plantcare.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;

import java.util.concurrent.Executors;

public class TaskActionReceiver extends BroadcastReceiver {
    public static final String ACTION_COMPLETE_TASK = "COMPLETE_TASK_LOCAL";

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("COMPLETE_TASK".equals(intent.getAction())) {
            int taskId = intent.getIntExtra("taskId", -1);
            if (taskId == -1) return;

            Intent localIntent = new Intent(ACTION_COMPLETE_TASK);
            localIntent.putExtra("taskId", taskId);
            LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
        }
    }
}
