package com.example.plantcare.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.plantcare.ui.main.MainActivity;
import com.example.plantcare.R;
import com.example.plantcare.data.entity.Task;

    public class NotificationHelper {
        private static final String CHANNEL_ID = "task_notification_channel";
        // Hằng số để tạo request code duy nhất cho action
        private static final int ACTION_REQUEST_CODE_OFFSET = 100000;

        public static void showTaskNotification(Context context, Task task) {
            createChannel(context);

            // Intent mở app
            Intent openIntent = new Intent(context, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(
                    context, 0, openIntent, PendingIntent.FLAG_IMMUTABLE
            );

            // Intent nút “Hoàn thành”
            Intent completeIntent = new Intent(context, TaskActionReceiver.class);
            completeIntent.setAction(TaskActionReceiver.ACTION_COMPLETE_TASK);
            completeIntent.putExtra("taskId", task.getTaskId());

            // Tạo một request code ĐỘC NHẤT cho action "Hoàn thành"
            // để tránh xung đột với request code của AlarmManager
            int completeRequestCode = task.getTaskId() + ACTION_REQUEST_CODE_OFFSET;

            PendingIntent completePendingIntent = PendingIntent.getBroadcast(
                    context,
                    completeRequestCode, // Sử dụng request code mới
                    completeIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            String title = "Đến giờ cho cây 🌿";
            String message = "Công việc: " + task.getName();

            Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setColor(Color.GREEN)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(contentIntent)
                    .setAutoCancel(true)
                    .addAction(R.drawable.ic_done, "Hoàn thành", completePendingIntent)
                    .build();

            Log.d("Alarm", "Showing notification for task " + task.getTaskId());
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(task.getTaskId(), notification);
        }

        private static void createChannel(Context context) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Nhắc công việc", NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Thông báo nhắc công việc chăm sóc cây");
            manager.createNotificationChannel(channel);
        }
    }
