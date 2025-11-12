package com.example.plantcare.ui.history;

import android.content.Context;import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {

    // Biến để lưu vị trí của item đang được chọn trong dropdown
    private int selectedPosition = -1;

    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Gọi phương thức gốc để lấy view cho item trong dropdown
        // Nó sẽ tự động xử lý việc inflate layout và tái sử dụng convertView
        View view = super.getDropDownView(position, convertView, parent);

        // Chuyển view thành TextView để có thể thay đổi thuộc tính
        TextView textView = (TextView) view;

        // --- LOGIC TÔ MÀU CHO ITEM ĐƯỢC CHỌN ---
        if (position == selectedPosition) {
            // Nếu là item đang được chọn, tô màu nền xám nhạt
            textView.setBackgroundColor(Color.parseColor("#E0E0E0"));
        } else {
            // Nếu không, đảm bảo nền trong suốt
            textView.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }

    /**
     * Phương thức công khai để cập nhật vị trí được chọn từ bên ngoài (từ Fragment)
     * @param position Vị trí mới được chọn.
     */
    public void setSelectedPosition(int position) {
        this.selectedPosition = position;
        // Không cần gọi notifyDataSetChanged() ở đây vì Spinner tự xử lý việc vẽ lại
    }
}
