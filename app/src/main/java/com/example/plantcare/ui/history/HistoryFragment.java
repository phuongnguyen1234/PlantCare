package com.example.plantcare.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.ui.main.BaseFragment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseFragment {

    private HistoryViewModel mViewModel;
    private ListView lvHistory;
    private HistoryAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Ghi đè onCreateView để có thể truy cập View ngay lập tức
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // Ánh xạ ListView từ layout đã sửa
        lvHistory = view.findViewById(R.id.lvHistory);

        // Tạo dữ liệu giả
        List<History> mockData = createMockData();

        // Khởi tạo Adapter với dữ liệu giả và gán vào ListView
        if (getContext() != null) {
            adapter = new HistoryAdapter(getContext(), mockData);
            lvHistory.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Sau này, bạn sẽ lấy dữ liệu từ ViewModel thay vì createMockData()
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_history;
    }

    @Override
    protected String getToolbarTitle() {
        return "Lịch sử chăm sóc";
    }

    /**
     * Phương thức tạo dữ liệu giả để kiểm tra giao diện.
     * @return Danh sách các đối tượng History.
     */
    private List<History> createMockData() {
        List<History> mockList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // --- Mẫu 1: Công việc TƯỚI NƯỚC - HOÀN THÀNH ---
        History taskDoneWater = new History();
        taskDoneWater.setTaskName("Tưới cây");
        taskDoneWater.setTaskType(TaskType.WATER);
        taskDoneWater.setStatus(Status.COMPLETED); // Trạng thái hoàn thành
        taskDoneWater.setNotifyTime(now.minusHours(5)); // Nhắc lúc 5 tiếng trước
        taskDoneWater.setDateCompleted(now.minusHours(4).minusMinutes(30)); // Hoàn thành sau đó 30 phút
        mockList.add(taskDoneWater);

        // --- Mẫu 2: Công việc BÓN PHÂN - BỎ LỠ ---
        History taskMissedFertilize = new History();
        taskMissedFertilize.setTaskName("Bón phân");
        taskMissedFertilize.setTaskType(TaskType.FERTILIZE);
        taskMissedFertilize.setStatus(Status.MISSED); // Trạng thái bỏ lỡ
        taskMissedFertilize.setNotifyTime(now.minusDays(1).minusHours(2)); // Nhắc từ hôm qua
        // dateCompleted là null vì đã bỏ lỡ
        mockList.add(taskMissedFertilize);

        // --- Mẫu 3: Công việc ÁNH SÁNG - HOÀN THÀNH ---
        History taskDoneLight = new History();
        taskDoneLight.setTaskName("Phơi nắng");
        taskDoneLight.setTaskType(TaskType.LIGHT);
        taskDoneLight.setStatus(Status.COMPLETED);
        taskDoneLight.setNotifyTime(now.minusDays(2).withHour(8).withMinute(0)); // Nhắc lúc 8h sáng 2 ngày trước
        taskDoneLight.setDateCompleted(now.minusDays(2).withHour(8).withMinute(15));
        mockList.add(taskDoneLight);

        // --- Mẫu 4: Công việc KHÁC - BỎ LỠ ---
        History taskMissedOther = new History();
        taskMissedOther.setTaskName("Kiểm tra sâu bệnh");
        taskMissedOther.setTaskType(TaskType.OTHER);
        taskMissedOther.setStatus(Status.MISSED);
        taskMissedOther.setNotifyTime(now.minusDays(3));
        mockList.add(taskMissedOther);

        return mockList;
    }
}
