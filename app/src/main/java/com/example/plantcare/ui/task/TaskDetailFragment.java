package com.example.plantcare.ui.task;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.DisplayableEnum;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.databinding.FragmentTaskDetailBinding;
import com.example.plantcare.ui.main.BaseFragment;
import com.example.plantcare.utils.DropdownUtils;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class TaskDetailFragment extends BaseFragment<FragmentTaskDetailBinding> {

    private TaskDetailViewModel viewModel;
    private static final String ARG_TASK_ID = "taskId";
    private int currentTaskId = -1;

    public static TaskDetailFragment newInstance(int taskId) {
        TaskDetailFragment fragment = new TaskDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_task_detail;
    }

    @Override
    protected String getToolbarTitle() {
        return ""; // Will be set by ViewModel
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        if (getArguments() != null) {
            currentTaskId = getArguments().getInt(ARG_TASK_ID, -1);
        }

        setupDropdowns();
        setupPlantCheckboxes();
        setupDateTimePickers();
        setupObservers();

        viewModel.start(currentTaskId);

        binding.addButton.setOnClickListener(v -> validateAndSave());
        binding.updateButton.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        TextInputLayout taskNameLayout = (TextInputLayout)binding.taskNameEdit.getParent().getParent();
        TextInputLayout taskTypeLayout = (TextInputLayout)binding.taskTypeDropdown.getParent().getParent();
        TextInputLayout notifyTimeLayout = (TextInputLayout)binding.notifyTimeEdit.getParent().getParent();

        taskNameLayout.setError(null);
        taskTypeLayout.setError(null);
        notifyTimeLayout.setError(null);
        notifyTimeLayout.setErrorIconDrawable(null);

        String taskName = binding.taskNameEdit.getText().toString().trim();
        String taskType = binding.taskTypeDropdown.getText().toString().trim();
        String notifyTime = binding.notifyTimeEdit.getText().toString().trim();
        Set<Integer> selectedPlants = viewModel.selectedPlantIds.getValue();

        boolean isValid = true;
        if (TextUtils.isEmpty(taskName)) {
            taskNameLayout.setError("Vui lòng nhập tên công việc");
            isValid = false;
        }
        if (TextUtils.isEmpty(taskType)) {
            taskTypeLayout.setError("Vui lòng chọn loại công việc");
            isValid = false;
        }
        if (TextUtils.isEmpty(notifyTime)) {
            notifyTimeLayout.setError("Vui lòng chọn thời gian");
            isValid = false;
        }
        if (selectedPlants == null || selectedPlants.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất một cây áp dụng", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (isValid) {
            Task task = buildTaskFromFields();
            if (viewModel.isEditMode.getValue() != null && viewModel.isEditMode.getValue()) {
                viewModel.onUpdateClicked(task);
            } else {
                viewModel.onAddClicked(task);
            }
        }
    }
    
    private Task buildTaskFromFields() {
        Task task = new Task();
        if (Boolean.TRUE.equals(viewModel.isEditMode.getValue())) {
            task.setTaskId(currentTaskId);
        }

        task.setName(binding.taskNameEdit.getText().toString());

        TaskType type = getEnumValueFromDisplayName(TaskType.class, binding.taskTypeDropdown.getText().toString());
        task.setType(type != null ? type : TaskType.OTHER);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            task.setNotifyTime(LocalDateTime.parse(binding.notifyTimeEdit.getText().toString(), dateTimeFormatter));
        } catch (Exception e) {
            task.setNotifyTime(LocalDateTime.now());
        }
        
        task.setRepeat(binding.repeatCheckbox.isChecked());

        if (task.isRepeat()) {
             try {
                task.setFrequency(Integer.parseInt(binding.frequencyEdit.getText().toString()));
            } catch (NumberFormatException e) { task.setFrequency(0); }
            
            FrequencyUnit unit = getEnumValueFromDisplayName(FrequencyUnit.class, binding.frequencyUnitDropdown.getText().toString());
            task.setFrequencyUnit(unit != null ? unit : FrequencyUnit.DAY);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            try {
                task.setNotifyStart(LocalTime.parse(binding.startTimeEdit.getText().toString(), timeFormatter).atDate(LocalDate.now()));
            } catch (Exception e) { task.setNotifyStart(null); }
            try {
                task.setNotifyEnd(LocalTime.parse(binding.endTimeEdit.getText().toString(), timeFormatter).atDate(LocalDate.now()));
            } catch (Exception e) { task.setNotifyEnd(null); }
        } else {
             task.setFrequency(0);
            task.setFrequencyUnit(FrequencyUnit.DAY);
            task.setNotifyStart(null);
            task.setNotifyEnd(null);
        }

        task.setNote(binding.noteEdit.getText().toString());
        task.setExpiration(task.getNotifyTime().plusHours(2));
        task.setStatus(Status.SCHEDULED);

        return task;
    }

    private <T extends Enum<T> & DisplayableEnum> T getEnumValueFromDisplayName(Class<T> enumClass, String displayName) {
        if (TextUtils.isEmpty(displayName) || enumClass.getEnumConstants() == null) {
            return null;
        }
        for (T enumValue : enumClass.getEnumConstants()) {
            if (Objects.equals(enumValue.getDisplayName(), displayName)) {
                return enumValue;
            }
        }
        return null;
    }

    private void setupObservers() {
        viewModel.toolbarTitle.observe(getViewLifecycleOwner(), title -> {
            TextView toolbarTitle = requireActivity().findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                toolbarTitle.setText(title);
            }
        });

        viewModel.navigateBack.observe(getViewLifecycleOwner(), navigate -> {
            if (navigate) {
                getParentFragmentManager().popBackStack();
                viewModel.onNavigatedBack();
            }
        });
        
        viewModel.selectedPlantIds.observe(getViewLifecycleOwner(), selectedIds -> {
            if (selectedIds == null) return;
            for (int i = 0; i < binding.plantsCheckboxContainer.getChildCount(); i++) {
                View child = binding.plantsCheckboxContainer.getChildAt(i);
                if (child instanceof MaterialCheckBox) {
                    MaterialCheckBox cb = (MaterialCheckBox) child;
                    int plantId = (int) cb.getTag();
                    cb.setOnCheckedChangeListener(null);
                    cb.setChecked(selectedIds.contains(plantId));
                    cb.setOnCheckedChangeListener((buttonView, isChecked) -> onPlantCheckboxChanged(isChecked, plantId));
                }
            }
        });

        viewModel.taskType.observe(getViewLifecycleOwner(), type -> {
            if (type != null) {
                binding.taskTypeDropdown.setText(type.getDisplayName(), false);
            }
        });

        viewModel.frequencyUnit.observe(getViewLifecycleOwner(), unit -> {
            if (unit != null) {
                binding.frequencyUnitDropdown.setText(unit.getDisplayName(), false);
            }
        });
    }

    private void setupDropdowns() {
        DropdownUtils.setupEnumDropdown(binding.taskTypeDropdown, TaskType.class);
        DropdownUtils.setupEnumDropdown(binding.frequencyUnitDropdown, FrequencyUnit.class);
    }

    private void setupPlantCheckboxes() {
        viewModel.allPlants.observe(getViewLifecycleOwner(), plants -> {
            if (plants == null) return;
            binding.plantsCheckboxContainer.removeAllViews();
            Set<Integer> currentlySelected = viewModel.selectedPlantIds.getValue();

            for (com.example.plantcare.data.entity.Plant plant : plants) {
                MaterialCheckBox checkBox = new MaterialCheckBox(requireContext());
                int plantId = plant.getPlantId();
                checkBox.setText(plant.getName());
                checkBox.setTag(plantId);

                if (currentlySelected != null && currentlySelected.contains(plantId)) {
                    checkBox.setChecked(true);
                }

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> onPlantCheckboxChanged(isChecked, plantId));
                binding.plantsCheckboxContainer.addView(checkBox);
            }
        });
    }

    private void onPlantCheckboxChanged(boolean isChecked, int plantId) {
        Set<Integer> selectedIds = viewModel.selectedPlantIds.getValue();
        if (selectedIds == null) {
            selectedIds = new HashSet<>();
        }
        if (isChecked) {
            selectedIds.add(plantId);
        } else {
            selectedIds.remove(plantId);
        }
        viewModel.selectedPlantIds.setValue(selectedIds);
    }

    private void setupDateTimePickers() {
        TextInputLayout notifyTimeLayout = (TextInputLayout) binding.notifyTimeEdit.getParent().getParent();
        notifyTimeLayout.setEndIconOnClickListener(v -> showDatePicker());

        binding.startTimeEdit.setOnClickListener(v -> showTimePickerDialog(binding.startTimeEdit));
        binding.endTimeEdit.setOnClickListener(v -> showTimePickerDialog(binding.endTimeEdit));
    }

    private void showDatePicker() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Chọn ngày")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            Instant instant = Instant.ofEpochMilli(selection);
            LocalDate selectedDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

            LocalTime now = LocalTime.now();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minute) -> {
                LocalTime selectedTime = LocalTime.of(hourOfDay, minute);
                LocalDateTime finalDateTime = LocalDateTime.of(selectedDate, selectedTime);
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                binding.notifyTimeEdit.setText(finalDateTime.format(formatter));

            }, now.getHour(), now.getMinute(), true);

            timePickerDialog.show();
        });

        datePicker.show(getParentFragmentManager(), "DATE_PICKER");
    }

    private void showTimePickerDialog(TextInputEditText timeEditText) {
        LocalTime now = LocalTime.now();
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view, hourOfDay, minuteOfHour) -> {
            LocalTime selectedTime = LocalTime.of(hourOfDay, minuteOfHour);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            timeEditText.setText(selectedTime.format(formatter));
        }, now.getHour(), now.getMinute(), true);

        timePickerDialog.show();
    }
}
