package com.example.plantcare.utils;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;

public class BindingAdapters {

    @BindingAdapter("imageUrl")
    public static void setPlantImage(ImageView imageView, String imageUrl) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setImageResource(R.drawable.plant_64);
        } else {
            Glide.with(imageView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.plant_64)
                    .error(R.drawable.plant_64)
                    .into(imageView);
        }
    }
}
