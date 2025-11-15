package com.example.plantcare.ui.history;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.plantcare.R;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.databinding.FragmentHistoryBinding;
import com.example.plantcare.databinding.HistoryFilterBottomSheetLayoutBinding;
import com.example.plantcare.ui.main.BaseFragment;
import com.example.plantcare.utils.DatePickerUtils;
import com.example.plantcare.utils.DropdownUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends BaseFragment<FragmentHistoryBinding> {

    private HistoryViewModel mViewModel;
    private HistoryAdapter mAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        mAdapter = new HistoryAdapter();
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvHistory.setAdapter(mAdapter);

        binding.btnHistoryFilter.setOnClickListener(v -> showFilterBottomSheet());

        binding.searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mViewModel.getHistories().observe(getViewLifecycleOwner(), histories -> {
            mAdapter.submitList(histories);
        });

        mViewModel.isFilterActive.observe(getViewLifecycleOwner(), isActive -> {
            int color = isActive ? ContextCompat.getColor(requireContext(), R.color.history_green) : ContextCompat.getColor(requireContext(), R.color.black);
            binding.btnHistoryFilter.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        });
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        HistoryFilterBottomSheetLayoutBinding bottomSheetBinding = HistoryFilterBottomSheetLayoutBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(bottomSheetBinding.getRoot());

        DropdownUtils.setupEnumDropdown(bottomSheetBinding.spnTaskType, TaskType.class);

        bottomSheetBinding.tvNotifyDate.setOnClickListener(v -> {
            LocalDate initialDate = LocalDate.now();
            String currentDateString = bottomSheetBinding.tvNotifyDate.getText().toString();
            DateTimeFormatter userFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            if (!currentDateString.isEmpty()) {
                try {
                    initialDate = LocalDate.parse(currentDateString, userFormatter);
                } catch (Exception e) {
                    // Ignore parse error, use today's date
                }
            }
            DatePickerUtils.showDatePickerDialog(
                    getContext(),
                    initialDate,
                    selectedDate -> bottomSheetBinding.tvNotifyDate.setText(selectedDate.format(userFormatter))
            );
        });

        HistoryViewModel.FilterParams currentFilter = mViewModel.getFilterParams().getValue();
        if (currentFilter != null && !currentFilter.isClear()) {
            bottomSheetBinding.filterSwitch.setChecked(true);
            if (currentFilter.taskType != null) {
                try {
                    TaskType taskType = TaskType.valueOf(currentFilter.taskType);
                    bottomSheetBinding.spnTaskType.setText(taskType.getDisplayName(), false);
                } catch (IllegalArgumentException e) {
                    // Handle case where taskType name from filter is not a valid enum constant
                    bottomSheetBinding.spnTaskType.setText("", false);
                }
            }
            if (currentFilter.date != null) {
                try {
                    SimpleDateFormat fromDb = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat toUser = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date parsedDate = fromDb.parse(currentFilter.date);
                    if (parsedDate != null) {
                        bottomSheetBinding.tvNotifyDate.setText(toUser.format(parsedDate));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (currentFilter.statuses != null) {
                bottomSheetBinding.cbDoneStatus.setChecked(currentFilter.statuses.contains(Status.COMPLETED.name()));
                bottomSheetBinding.cbMissStatus.setChecked(currentFilter.statuses.contains(Status.MISSED.name()));
            }
        } else {
            bottomSheetBinding.filterSwitch.setChecked(false);
        }

        Runnable setFilterControlsEnabled = () -> {
            boolean isEnabled = bottomSheetBinding.filterSwitch.isChecked();
            for (int i = 0; i < bottomSheetBinding.filterContentGroup.getChildCount(); i++) {
                View child = bottomSheetBinding.filterContentGroup.getChildAt(i);
                child.setEnabled(isEnabled);
                child.setAlpha(isEnabled ? 1.0f : 0.5f);
            }
            bottomSheetBinding.btnApplyFilter.setEnabled(isEnabled);
        };

        setFilterControlsEnabled.run();

        bottomSheetBinding.filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setFilterControlsEnabled.run();
            if (!isChecked) {
                bottomSheetBinding.spnTaskType.setText("", false);
                bottomSheetBinding.tvNotifyDate.setText("");
                bottomSheetBinding.cbDoneStatus.setChecked(false);
                bottomSheetBinding.cbMissStatus.setChecked(false);
                mViewModel.setFilter(null, null, null);
            }
        });

        bottomSheetBinding.btnApplyFilter.setOnClickListener(v -> {
            String selectedDisplayName = bottomSheetBinding.spnTaskType.getText().toString();
            TaskType selectedTaskType = DropdownUtils.getEnumValueFromDisplayName(TaskType.class, selectedDisplayName);
            String taskTypeName = (selectedTaskType != null) ? selectedTaskType.name() : null;

            List<String> statuses = new ArrayList<>();
            if (bottomSheetBinding.cbDoneStatus.isChecked()) {
                statuses.add(Status.COMPLETED.name());
            }
            if (bottomSheetBinding.cbMissStatus.isChecked()) {
                statuses.add(Status.MISSED.name());
            }

            String date = null;
            String inputDate = bottomSheetBinding.tvNotifyDate.getText().toString();
            if (!inputDate.isEmpty()) {
                try {
                    SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    SimpleDateFormat toDb = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date parsedDate = fromUser.parse(inputDate);
                    if (parsedDate != null) {
                        date = toDb.format(parsedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Invalid date format", Toast.LENGTH_SHORT).show();
                }
            }

            mViewModel.setFilter(taskTypeName, statuses, date);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_history;
    }

    @Override
    protected String getToolbarTitle() {
        return "Lịch sử chăm sóc";
    }
}
