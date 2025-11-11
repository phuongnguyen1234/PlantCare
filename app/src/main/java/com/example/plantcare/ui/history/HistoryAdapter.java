package com.example.plantcare.ui.history;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.plantcare.R; // Thêm import R
import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.enums.Status; // Import Enum Status
import com.example.plantcare.data.enums.TaskType; // Import Enum TaskType

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryAdapter extends ArrayAdapter<History> {

    public HistoryAdapter(Context context, List<History> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            // SỬA LẠI TÊN LAYOUT CHO ĐÚNG
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragmennt_history_plant, parent, false);
        }
        History item = getItem(position);
        // Ánh xạ lại các view cho đúng với ID trong file XML
        LinearLayout layout = convertView.findViewById(R.id.itemLayout);
        TextView txtTaskName = convertView.findViewById(R.id.txtTasKName); // Sửa ID
        TextView txtPlantName = convertView.findViewById(R.id.txtPlantName);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtDateFinish = convertView.findViewById(R.id.txtDateFinish); // Thêm TextView này
        ImageView imgAction = convertView.findViewById(R.id.imgAction);
        ImageView imgStatus = convertView.findViewById(R.id.imgStatus);
        TextView txtMiss = convertView.findViewById(R.id.txtMiss);

        // Định dạng thời gian cho dễ đọc
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

        // Gán dữ liệu
        txtTaskName.setText(item.getTaskName()); // Tên công việc (Bón phân, tưới nước...)
        txtPlantName.setText(item.getTaskName()); // Tạm thời hardcode, bạn có thể thêm trường plantName vào History entity
        txtDate.setText("Đã nhắc lúc: " + item.getNotifyTime().format(formatter));

        switch (item.getTaskType()) {
            case WATER:
                imgAction.setImageResource(R.drawable.water_drop);
                break;
            case FERTILIZE:
                imgAction.setImageResource(R.drawable.fertilizer);
                break;
            case LIGHT:
                imgAction.setImageResource(R.drawable.sun);
                break;
            case OTHER:
                imgAction.setImageResource(R.drawable.default_plant);
                break;
            default:
                imgAction.setImageResource(R.drawable.water_drop);
        }

        if (item.getStatus() == Status.COMPLETED && item.getDateCompleted() != null) {
            // TRẠNG THÁI: HOÀN THÀNH
            layout.setBackgroundResource(R.drawable.history_done_background_green);
            imgStatus.setVisibility(View.VISIBLE);
            txtMiss.setVisibility(View.GONE);
            txtDateFinish.setVisibility(View.VISIBLE);
            txtDateFinish.setText("Hoàn thành: " + item.getDateCompleted().format(formatter));
        } else {
            // TRẠNG THÁI: BỎ LỠ
            layout.setBackgroundResource(R.drawable.history_miss_item_background_orange);
            imgStatus.setVisibility(View.GONE);
            txtMiss.setVisibility(View.VISIBLE);
            txtDateFinish.setVisibility(View.GONE);
        }

        return convertView;
    }
}
