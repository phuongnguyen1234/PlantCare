package com.example.plantcare.notification;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.ui.task.TaskViewModel;

import java.util.concurrent.Executors;

public class TaskActionReceiver extends BroadcastReceiver {
    public static final String ACTION_COMPLETE_TASK = "COMPLETE_TASK";
    // Vẫn giữ lại action này để báo cho UI biết cần cập nhật
    public static final String ACTION_UI_TASK_COMPLETE = "TASK_ACTION_COMPLETE";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Chỉ xử lý action mong muốn, bỏ qua nếu intent hoặc action là null
        if (intent == null || intent.getAction() == null) {
            return;
        }

        final String action = intent.getAction();
        final int taskId = intent.getIntExtra("taskId", -1);
        if (taskId == -1) return;

        // Chỉ xử lý action "COMPLETE_TASK"
        if (ACTION_COMPLETE_TASK.equals(action)) {
            // Chạy tác vụ trên một luồng nền để tránh chặn BroadcastReceiver
            Executors.newSingleThreadExecutor().execute(() -> {
                // Lấy task từ database
                TaskDao dao = AppDatabase.getDatabase(context.getApplicationContext()).taskDao();
                Task task = dao.getTaskSync(taskId);
                if (task == null) {
                    Log.e("TaskActionReceiver", "Task with ID " + taskId + " not found.");
                    return;
                }

                // 1. GỌI LOGIC XỬ LÝ: Hoàn thành công việc, ghi history, reschedule (nếu cần)
                // Đây là bước quan trọng nhất bị thiếu
                TaskViewModel.processTaskStatic(context, task, true);

                // 2. Hủy thông báo sau khi đã xử lý
                NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (nm != null) {
                    nm.cancel(taskId);
                }

                // 3. Gửi broadcast để thông báo cho UI (nếu đang mở) là cần cập nhật
                Intent uiUpdateIntent = new Intent(ACTION_UI_TASK_COMPLETE);
                uiUpdateIntent.putExtra("taskId", taskId);
                LocalBroadcastManager.getInstance(context).sendBroadcast(uiUpdateIntent);
            });
        }
    }
}
