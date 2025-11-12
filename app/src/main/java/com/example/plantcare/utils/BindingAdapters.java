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
}
