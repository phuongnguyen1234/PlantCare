package com.example.plantcare.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.google.android.material.chip.Chip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BindingAdapters {
    @BindingAdapter(value = {"imageUrl", "isListItem"}, requireAll = false)
    public static void loadImage(ImageView view, String url, boolean isListItem) {
        // Phương thức này được dùng ở cả màn hình Add/Edit và RecyclerView
        if (url != null && !url.isEmpty()) {
            // SỬA LỖI: Xóa tint trước khi tải ảnh
            view.setImageTintList(null);
            Glide.with(view.getContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.plant_64) // Placeholder cho danh sách
                    .error(R.drawable.plant_64) // Hiển thị placeholder nếu URL lỗi
                    .into(view);
        } else {
            if (isListItem) {
                view.setImageResource(R.drawable.plant_64);
                view.setImageTintList(null); // Không cần tint cho placeholder
            } else {
                // Nếu không có URL, hiển thị placeholder mặc định.
                // Trạng thái ban đầu (với icon camera) được set trong file XML.
                // Trạng thái trong danh sách sẽ là ảnh placeholder này.
                view.setImageResource(android.R.drawable.ic_menu_camera);
                view.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(view.getContext(), android.R.color.darker_gray)));
            }
        }
    }

    @BindingAdapter("taskIcon")
    public static void setTaskIcon(ImageView imageView, TaskType taskType) {
        if (taskType == null) {
            imageView.setImageResource(R.drawable.task_dashboard);
            return;
        }

        switch (taskType) {
            case WATER:
                imageView.setImageResource(R.drawable.water_drop);
                break;
            case FERTILIZE:
                imageView.setImageResource(R.drawable.fertilizer);
                break;
            case LIGHT:
                imageView.setImageResource(R.drawable.sun);
                break;
            case OTHER:
            default:
                imageView.setImageResource(R.drawable.task_dashboard);
                break;
        }
    }

    @BindingAdapter("formattedDateTime")
    public static void setFormattedDateTime(TextView textView, LocalDateTime dateTime) {
        if (dateTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            textView.setText(dateTime.format(formatter));
        } else {
            textView.setText("");
        }
    }

    @BindingAdapter("chipStatusColor")
    public static void setChipStatusColor(Chip chip, Status status) {
        if (status == null) return;

        int colorRes;
        if (status == Status.READY) {
            // A light blue color for "Sẵn sàng"
            colorRes = Color.parseColor("#B3E5FC"); // Light Blue 100
        } else {
            // A neutral gray for other statuses
            colorRes = Color.parseColor("#F5F5F5"); // Grey 100
        }
        chip.setChipBackgroundColor(ColorStateList.valueOf(colorRes));
    }
}
