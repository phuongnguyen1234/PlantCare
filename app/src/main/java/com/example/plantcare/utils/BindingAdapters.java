package com.example.plantcare.utils;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;

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
}
