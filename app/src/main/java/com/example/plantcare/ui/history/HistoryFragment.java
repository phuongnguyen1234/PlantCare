package com.example.plantcare.ui.history;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.History;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.ui.main.BaseFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends BaseFragment {

    private HistoryViewModel mViewModel;
    private ListView lvHistory;
    private HistoryAdapter adapter;
    private ImageButton filterButton, btnSortList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Ghi đè onCreateView để có thể truy cập View ngay lập tức
        View view = super.onCreateView(inflater, container, savedInstanceState);
        lvHistory = view.findViewById(R.id.lvHistory);
        filterButton = view.findViewById(R.id.btnHistoryFilter);
        btnSortList = view.findViewById(R.id.btnSortList);
        // Tạo dữ liệu giả
        List<History> mockData = createMockData();

        // Khởi tạo Adapter với dữ liệu giả và gán vào ListView
        if (getContext() != null) {
            adapter = new HistoryAdapter(getContext(), mockData);
            lvHistory.setAdapter(adapter);
        }
        registerForContextMenu(btnSortList);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog booBottomSheetDialog = new BottomSheetDialog(requireContext());
                View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_layout, null);
                booBottomSheetDialog.setContentView(bottomSheetView);
                booBottomSheetDialog.show();
                Button applyFilterButton = bottomSheetView.findViewById(R.id.btnApplyFilter);

                // gan dữ liệu cho ddl menu
                Spinner spinner = booBottomSheetDialog.findViewById(R.id.spnTaskType);
                String[] tasks = {"Tưới nước", "Ánh sáng", "Bón phân", "Khác"};
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                        requireContext(),
                        android.R.layout.simple_spinner_item, // Layout cho item đã chọn (khi Spinner đóng)
                        tasks
                );
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Layout cho các item trong danh sách
                spinner.setAdapter(spinnerAdapter);
                spinner.setAdapter(spinnerAdapter);


                applyFilterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        booBottomSheetDialog.dismiss();
                    }
                });

                booBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        Toast.makeText(requireContext(), "close bottomSheet", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

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
    // tạo context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.history_context_menu, menu);
    }
    // xử lý sự kiện khi chọn item trong context menu
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int menuItemSelected = item.getItemId();
        if(menuItemSelected  == R.id.menu_history_sort_name){
            Toast.makeText(requireContext(), "sort by name", Toast.LENGTH_SHORT).show();
        }
        return super.onContextItemSelected(item);
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
