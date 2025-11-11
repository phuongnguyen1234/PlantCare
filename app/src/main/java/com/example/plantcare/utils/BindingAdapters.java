package com.example.plantcare.utils;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;

public class BindingAdapters {
    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView view, Uri uri) {
        view.setPadding(0, 0, 0, 0);
        if (uri != null) {
            view.setImageTintList(null);
            Glide.with(view.getContext())
                    .load(uri)
                    .centerCrop()
                    .into(view);
        } else {
            view.setImageResource(android.R.drawable.ic_menu_camera);
            int darkerGray = view.getContext().getResources().getColor(android.R.color.darker_gray);
            view.setImageTintList(ColorStateList.valueOf(darkerGray));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
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
}
