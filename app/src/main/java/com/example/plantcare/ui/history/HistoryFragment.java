package com.example.plantcare.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.databinding.FragmentHistoryBinding; // Import lớp binding đúng

import com.example.plantcare.ui.main.BaseFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// SỬA 1: Cung cấp kiểu generic FragmentHistoryBinding cho BaseFragment
public class HistoryFragment extends BaseFragment<FragmentHistoryBinding> {

    private HistoryViewModel mViewModel;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // Gọi super.onViewCreated() để thiết lập Toolbar và nút Back
        super.onViewCreated(view, savedInstanceState);

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        setHistoryDataToRecycleView();

        // Thêm listener cho nút filter
        binding.btnHistoryFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.history_filter_bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Button applyFilterButton = bottomSheetView.findViewById(R.id.btnApplyFilter);
        Spinner spinner = bottomSheetView.findViewById(R.id.spnTaskType);

        String[] tasks = {"Tưới nước", "Ánh sáng", "Bón phân", "Khác"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                tasks
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        applyFilterButton.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Áp dụng bộ lọc...", Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        // TODO: Quan sát LiveData từ ViewModel và gọi adapter.submitList(newList)
    }

    @Override
    protected int getLayoutResourceId() {
        // SỬA 3: Cung cấp ID layout chính xác cho BaseFragment
        return R.layout.fragment_history;
    }

    @Override
    protected String getToolbarTitle() {
        return "Lịch sử chăm sóc";
    }

    private void setHistoryDataToRecycleView(){
        // 'binding' đã được khởi tạo trong BaseFragment nên bạn có thể dùng trực tiếp
        HistoryViewModelApdapter historyViewModelApdapter = new HistoryViewModelApdapter(getListHistory());
        binding.rvHistory.setAdapter(historyViewModelApdapter);
    }
    private List<History> getListHistory(){
        return createMockData();
    }
    private List<History> createMockData() {
        List<History> mockList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        History taskDoneWater = new History();
        taskDoneWater.setHistoryId(1);
        taskDoneWater.setTaskName("Tưới cây");
        taskDoneWater.setTaskType(TaskType.WATER);
        taskDoneWater.setStatus(Status.COMPLETED);
        taskDoneWater.setNotifyTime(now.minusHours(5));
        taskDoneWater.setDateCompleted(now.minusHours(4).minusMinutes(30));
        mockList.add(taskDoneWater);

        History taskMissedFertilize = new History();
        taskMissedFertilize.setHistoryId(2);
        taskMissedFertilize.setTaskName("Bón phân");
        taskMissedFertilize.setTaskType(TaskType.FERTILIZE);
        taskMissedFertilize.setStatus(Status.MISSED);
        taskMissedFertilize.setNotifyTime(now.minusDays(1).minusHours(2));
        mockList.add(taskMissedFertilize);

        History taskDoneLight = new History();
        taskDoneLight.setHistoryId(3);
        taskDoneLight.setTaskName("Phơi nắng cho cây ABC");
        taskDoneLight.setTaskType(TaskType.LIGHT);
        taskDoneLight.setStatus(Status.COMPLETED);
        taskDoneLight.setNotifyTime(now.minusDays(2).withHour(8).withMinute(0));
        taskDoneLight.setDateCompleted(now.minusDays(2).withHour(8).withMinute(15));
        mockList.add(taskDoneLight);

        History taskMissedOther = new History();
        taskMissedOther.setHistoryId(4);
        taskMissedOther.setTaskName("Kiểm tra sâu bệnh");
        taskMissedOther.setTaskType(TaskType.OTHER);
        taskMissedOther.setStatus(Status.MISSED);
        taskMissedOther.setNotifyTime(now.minusDays(3));
        mockList.add(taskMissedOther);

        return mockList;
    }
}
