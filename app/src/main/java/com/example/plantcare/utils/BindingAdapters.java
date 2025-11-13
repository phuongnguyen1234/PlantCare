package com.example.plantcare.utils;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.enums.TaskType;

public class BindingAdapters {
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, Uri uri) {
        view.setPadding(0, 0, 0, 0);
        view.setImageTintList(null);
        Glide.with(view.getContext())
                .load(uri)
                .centerCrop()
                .into(view);
    }

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, String url) {
        if (url != null && !url.isEmpty()) {
            Glide.with(view.getContext())
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.plant_64)
                    .into(view);
        } else {
            view.setImageResource(R.drawable.plant_64);
        }
    }
    @BindingAdapter("taskTypeImage")
    public static void setTaskTypeImage(ImageView imageView, TaskType taskType) {
        if (taskType == null) {
            imageView.setImageResource(R.drawable.default_plant); // Ảnh mặc định
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
                imageView.setImageResource(R.drawable.default_plant); // Mặc định cho trường hợp khác
                break;
        }
    }
}