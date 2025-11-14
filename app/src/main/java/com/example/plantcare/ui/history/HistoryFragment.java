package com.example.plantcare.ui.history;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.example.plantcare.ui.main.BaseFragment;
import com.example.plantcare.utils.DropdownUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.history_filter_bottom_sheet_layout, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        SwitchMaterial filterSwitch = bottomSheetView.findViewById(R.id.filterSwitch);
        LinearLayout filterContentGroup = bottomSheetView.findViewById(R.id.filterContentGroup);
        Button applyFilterButton = bottomSheetView.findViewById(R.id.btnApplyFilter);

        AutoCompleteTextView taskTypeTextView = bottomSheetView.findViewById(R.id.spnTaskType);
        TextInputEditText notifyDateEditText = bottomSheetView.findViewById(R.id.tvNotifyDate);
        MaterialCheckBox doneCheckbox = bottomSheetView.findViewById(R.id.cbDoneStatus);
        MaterialCheckBox missCheckbox = bottomSheetView.findViewById(R.id.cbMissStatus);

        DropdownUtils.setupEnumDropdown(taskTypeTextView, TaskType.class);

        HistoryViewModel.FilterParams currentFilter = mViewModel.getFilterParams().getValue();
        if (currentFilter != null && !currentFilter.isClear()) {
            filterSwitch.setChecked(true);
            if (currentFilter.taskType != null) {
                try {
                    TaskType taskType = TaskType.valueOf(currentFilter.taskType);
                    taskTypeTextView.setText(taskType.getDisplayName(), false);
                } catch (IllegalArgumentException e) {
                    // Handle case where taskType name from filter is not a valid enum constant
                    taskTypeTextView.setText("", false);
                }
            }
            if (currentFilter.date != null) {
                try {
                    SimpleDateFormat fromDb = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat toUser = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date parsedDate = fromDb.parse(currentFilter.date);
                    if (parsedDate != null) {
                        notifyDateEditText.setText(toUser.format(parsedDate));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (currentFilter.statuses != null) {
                doneCheckbox.setChecked(currentFilter.statuses.contains(Status.COMPLETED.name()));
                missCheckbox.setChecked(currentFilter.statuses.contains(Status.MISSED.name()));
            }
        } else {
            filterSwitch.setChecked(false);
        }

        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            notifyDateEditText.setText(sdf.format(calendar.getTime()));
        };

        notifyDateEditText.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        Runnable setFilterControlsEnabled = () -> {
            boolean isEnabled = filterSwitch.isChecked();
            for (int i = 0; i < filterContentGroup.getChildCount(); i++) {
                View child = filterContentGroup.getChildAt(i);
                child.setEnabled(isEnabled);
                child.setAlpha(isEnabled ? 1.0f : 0.5f);
            }
            applyFilterButton.setEnabled(isEnabled);
        };

        setFilterControlsEnabled.run();

        filterSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setFilterControlsEnabled.run();
            if (!isChecked) {
                taskTypeTextView.setText("", false);
                notifyDateEditText.setText("");
                doneCheckbox.setChecked(false);
                missCheckbox.setChecked(false);
                mViewModel.setFilter(null, null, null);
            }
        });

        applyFilterButton.setOnClickListener(v -> {
            String selectedDisplayName = taskTypeTextView.getText().toString();
            TaskType selectedTaskType = DropdownUtils.getEnumValueFromDisplayName(TaskType.class, selectedDisplayName);
            String taskTypeName = (selectedTaskType != null) ? selectedTaskType.name() : null;

            List<String> statuses = new ArrayList<>();
            if (doneCheckbox.isChecked()) {
                statuses.add(Status.COMPLETED.name());
            }
            if (missCheckbox.isChecked()) {
                statuses.add(Status.MISSED.name());
            }

            String date = null;
            String inputDate = notifyDateEditText.getText().toString();
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
