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

            // 1. Tạo Intent để mở MainActivity
            Intent openIntent = new Intent(context, MainActivity.class);

            // 2. Thêm một "dấu hiệu" để MainActivity biết cần mở TaskFragment
            openIntent.setAction("SHOW_TASK_FRAGMENT"); // Đặt một action rõ ràng
            openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa các activity cũ và tạo task mới

            // 3. Tạo PendingIntent
            // Request code có thể là 0 hoặc một số duy nhất nếu bạn có nhiều loại PendingIntent mở app
            PendingIntent contentIntent = PendingIntent.getActivity(
                    context,
                    task.getTaskId(), // Sử dụng taskId để mỗi thông báo có 1 pending intent riêng
                    openIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
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

            String title = "Bạn ơi! Cây của bạn đang cần được chăm sóc!";
            String message = task.getName() + " đã sẵn sàng để hoàn thành";

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
