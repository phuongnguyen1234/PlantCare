package com.example.plantcare.ui.history;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.plantcare.R;
import com.example.plantcare.ui.main.ToolbarAndNavControl;

public class HistoryFragment extends Fragment {

    private HistoryViewModel mViewModel;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Use the ViewModel
    }


    // 2. Ghi đè onResume để ẨN Bottom Nav
    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof ToolbarAndNavControl) {
            // "Báo" cho Activity biết: "Tôi đang hiển thị, hãy ẩn Bottom Nav đi"
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(false);
        }
    }

    // 3. Ghi đè onPause để HIỆN LẠI Bottom Nav
    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() instanceof ToolbarAndNavControl) {
            // "Báo" cho Activity biết: "Tôi sắp bị che khuất/hủy, hãy hiện lại Bottom Nav"
            ((ToolbarAndNavControl) getActivity()).showToolbarAndNav(true);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // === PHẦN SỬA LỖI QUAN TRỌNG NHẤT ===
        // Áp dụng Window Insets để nội dung của Fragment không bị đè lên bởi các thanh hệ thống.
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            // Lấy thông tin về kích thước của các thanh hệ thống.
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Áp dụng padding cho view gốc của Fragment.
            // Điều này sẽ đẩy nội dung xuống dưới status bar và lên trên navigation bar (nếu có).
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);

            // Trả về insets đã được xử lý để các view con không nhận lại nữa.
            return WindowInsetsCompat.CONSUMED;
        });
    }


}