package com.example.plantcare.notification;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.ui.task.TaskViewModel;

import java.util.concurrent.Executors;

public class TaskActionReceiver extends BroadcastReceiver {
    public static final String ACTION_COMPLETE_TASK = "COMPLETE_TASK";
    public static final String ACTION_EXPIRE_TASK = "EXPIRE_TASK";
    // Đây là Action mà TaskFragment sẽ lắng nghe
    public static final String ACTION_PROCESS_TASK_FROM_NOTIFICATION = "PROCESS_TASK_FROM_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Chỉ xử lý action mong muốn, bỏ qua nếu intent hoặc action là null
        if (intent == null || intent.getAction() == null) {
            return;
        }

        final String action = intent.getAction();
        final int taskId = intent.getIntExtra("taskId", -1);
        if (taskId == -1) return;

        Executors.newSingleThreadExecutor().execute(() -> {
            TaskDao dao = AppDatabase.getDatabase(context).taskDao();
            Task task = dao.getTaskSync(taskId);
            if (task == null) return;

            // 1. Hủy thông báo ngay lập tức
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.cancel(taskId);

            // 2. Chỉ xử lý ACTION_COMPLETE_TASK từ notification
            if (ACTION_COMPLETE_TASK.equals(action)) {
                // --- BẮT ĐẦU SỬA ĐỔI ---
                // Tạo một Intent mới để gửi cho LocalBroadcastManager
                // Giao nhiệm vụ xử lý logic cho TaskFragment/ViewModel
                Intent localIntent = new Intent(ACTION_PROCESS_TASK_FROM_NOTIFICATION);
                localIntent.putExtra("taskId", taskId);
                // Gửi broadcast cục bộ, chỉ trong phạm vi ứng dụng
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
                // --- KẾT THÚC SỬA ĐỔI ---
            }
            // Logic cho ACTION_EXPIRE_TASK có thể được xử lý riêng nếu cần
        });
    }
}
