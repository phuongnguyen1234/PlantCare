package com.example.plantcare.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.google.android.material.chip.Chip;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BindingAdapters {
    @BindingAdapter(value = {"imageUrl", "isListItem"}, requireAll = false)
    public static void loadImage(ImageView view, String url, boolean isListItem) {
        if (url != null && !url.isEmpty()) {
            view.setImageTintList(null);
            Glide.with(view.getContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.plant_64)
                    .error(R.drawable.plant_64)
                    .into(view);
        } else {
            if (isListItem) {
                view.setImageResource(R.drawable.plant_64);
                view.setImageTintList(null);
            } else {
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
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
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
            colorRes = Color.parseColor("#B3E5FC");
        } else {
            colorRes = Color.parseColor("#F5F5F5");
        }
        chip.setChipBackgroundColor(ColorStateList.valueOf(colorRes));
    }
    @BindingAdapter("taskTypeImage")
    public static void setTaskTypeImage(ImageView imageView, TaskType taskType) {
        if (taskType == null) {
            imageView.setImageResource(R.drawable.default_plant);
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
            default:
                imageView.setImageResource(R.drawable.task_dashboard);
                break;
        }
    }

    @BindingAdapter("formattedDate")
    public static void setFormattedDate(TextView view, LocalDateTime date) {
        if (date != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            view.setText(date.format(formatter));
        }
    }

    @BindingAdapter("listData")
    public static <T> void setRecyclerViewData(RecyclerView recyclerView, List<T> data) {
        if (recyclerView.getAdapter() instanceof ListAdapter) {
            @SuppressWarnings("unchecked")
            ListAdapter<T, ?> adapter = (ListAdapter<T, ?>) recyclerView.getAdapter();
            adapter.submitList(data);
        }
    }
}
