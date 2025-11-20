package com.example.plantcare.ui.task;

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
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.entity.Task;
import com.example.plantcare.data.enums.DisplayableEnum;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.data.enums.Status;
import com.example.plantcare.data.enums.TaskType;
import com.example.plantcare.databinding.FragmentTaskDetailBinding;
import com.example.plantcare.ui.main.BaseFragment;
import com.example.plantcare.utils.DatePickerUtils;
import com.example.plantcare.utils.DropdownUtils;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTaskId = getArguments().getInt(ARG_TASK_ID, -1);
        }
    }

    @Override
    protected String getToolbarTitle() {
        return currentTaskId == -1 ? "Thêm công việc" : "Sửa công việc";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TaskDetailViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        setupDropdowns();
        setupDateTimePickers();
        setupObservers();

        viewModel.start(currentTaskId);

        binding.saveButton.setOnClickListener(v -> validateAndSave());
        binding.selectAllPlantsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.onSelectAllChanged(isChecked);
        });
    }

    private void validateAndSave() {
        TextInputLayout taskNameLayout = (TextInputLayout)binding.taskNameEdit.getParent().getParent();
        TextInputLayout taskTypeLayout = (TextInputLayout)binding.taskTypeDropdown.getParent().getParent();
        TextInputLayout notifyTimeLayout = (TextInputLayout)binding.notifyTimeEdit.getParent().getParent();
        TextInputLayout frequencyLayout = (TextInputLayout) binding.frequencyEdit.getParent().getParent();
        TextInputLayout frequencyUnitLayout = (TextInputLayout) binding.frequencyUnitDropdown.getParent().getParent();

        taskNameLayout.setError(null);
        taskTypeLayout.setError(null);
        notifyTimeLayout.setError(null);
        notifyTimeLayout.setErrorIconDrawable(null);
        frequencyLayout.setError(null);
        frequencyUnitLayout.setError(null);

        String taskName = binding.taskNameEdit.getText().toString().trim();
        String taskType = binding.taskTypeDropdown.getText().toString().trim();
        String notifyTimeStr = binding.notifyTimeEdit.getText().toString().trim();
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
        if (TextUtils.isEmpty(notifyTimeStr)) {
            notifyTimeLayout.setError("Vui lòng chọn thời gian");
            isValid = false;
        }
        if (selectedPlants == null || selectedPlants.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn ít nhất một cây áp dụng", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        if (binding.repeatCheckbox.isChecked()) {
            if (TextUtils.isEmpty(binding.frequencyEdit.getText().toString())) {
                frequencyLayout.setError("Vui lòng nhập tần suất");
                isValid = false;
            }
            if (TextUtils.isEmpty(binding.frequencyUnitDropdown.getText().toString())) {
                frequencyUnitLayout.setError("Vui lòng chọn đơn vị");
                isValid = false;
            }
        }

        if (isValid) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            try {
                LocalDateTime selectedDateTime = LocalDateTime.parse(notifyTimeStr, formatter);
                // Only check for past time if it's a new task
                if (currentTaskId == -1 && selectedDateTime.isBefore(LocalDateTime.now())) {
                    notifyTimeLayout.setError("Không thể đặt thông báo cho thời gian trong quá khứ");
                    isValid = false;
                }
            } catch (Exception e) {
                notifyTimeLayout.setError("Định dạng thời gian không hợp lệ");
                isValid = false;
            }
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

        TaskType type = DropdownUtils.getEnumValueFromDisplayName(TaskType.class, binding.taskTypeDropdown.getText().toString());
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
            
            FrequencyUnit unit = DropdownUtils.getEnumValueFromDisplayName(FrequencyUnit.class, binding.frequencyUnitDropdown.getText().toString());
            task.setFrequencyUnit(unit != null ? unit : FrequencyUnit.HOUR);

            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            try {
                task.setNotifyStart(LocalTime.parse(binding.startTimeEdit.getText().toString(), timeFormatter).atDate(LocalDate.now()));
            } catch (Exception e) { task.setNotifyStart(null); }
            try {
                task.setNotifyEnd(LocalTime.parse(binding.endTimeEdit.getText().toString(), timeFormatter).atDate(LocalDate.now()));
            } catch (Exception e) { task.setNotifyEnd(null); }
        } else {
            task.setFrequency(0);
            task.setFrequencyUnit(FrequencyUnit.HOUR);
            task.setNotifyStart(null);
            task.setNotifyEnd(null);
        }

        task.setNote(binding.noteEdit.getText().toString());
        task.setExpiration(task.getNotifyTime().plusHours(1));
        task.setStatus(Status.SCHEDULED);

        return task;
    }

    private void setupObservers() {
        viewModel.navigateBack.observe(getViewLifecycleOwner(), navigate -> {
            if (navigate != null && navigate) {
                getParentFragmentManager().popBackStack();
                viewModel.onNavigatedBack();
            }
        });

        viewModel.toastMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                viewModel.onToastMessageShown();
            }
        });
        
        viewModel.selectedPlantIds.observe(getViewLifecycleOwner(), selectedIds -> {
            if (selectedIds == null) return;
            updatePlantCheckboxes(selectedIds);
            viewModel.checkSelectAllState(); // Check after selection changes
        });

        viewModel.selectAllPlants.observe(getViewLifecycleOwner(), isSelected -> {
            // Temporarily remove the listener to prevent loops
            binding.selectAllPlantsCheckbox.setOnCheckedChangeListener(null);
            binding.selectAllPlantsCheckbox.setChecked(isSelected);
            binding.selectAllPlantsCheckbox.setOnCheckedChangeListener((buttonView, isChecked_)
                    -> viewModel.onSelectAllChanged(isChecked_));
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

        viewModel.allPlants.observe(getViewLifecycleOwner(), plants -> {
            if (plants == null || plants.isEmpty()) {
                Toast.makeText(getContext(), "Không có cây nào để tạo hoặc sửa công việc.", Toast.LENGTH_LONG).show();
                getParentFragmentManager().popBackStack();
            } else {
                setupPlantCheckboxesWithData(plants);
                viewModel.checkSelectAllState(); // Check after all plants are loaded
                if (currentTaskId == -1) { // If in "add mode"
                    viewModel.onSelectAllChanged(true); // Select all by default
                }
            }
        });
    }

    private void setupDropdowns() {
        DropdownUtils.setupEnumDropdown(binding.taskTypeDropdown, TaskType.class);
        DropdownUtils.setupEnumDropdown(binding.frequencyUnitDropdown, FrequencyUnit.class);
    }

    private void setupPlantCheckboxesWithData(List<Plant> plants) {
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

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.onPlantSelectionChanged(plantId, isChecked));
            binding.plantsCheckboxContainer.addView(checkBox);
        }
    }
    
    private void updatePlantCheckboxes(Set<Integer> selectedIds) {
        if (selectedIds == null) return;
        for (int i = 0; i < binding.plantsCheckboxContainer.getChildCount(); i++) {
            View child = binding.plantsCheckboxContainer.getChildAt(i);
            if (child instanceof MaterialCheckBox) {
                MaterialCheckBox cb = (MaterialCheckBox) child;
                int plantId = (int) cb.getTag();
                cb.setOnCheckedChangeListener(null); // Avoid triggering listener
                cb.setChecked(selectedIds.contains(plantId));
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.onPlantSelectionChanged(plantId, isChecked));
            }
        }
    }

    private void setupDateTimePickers() {
        TextInputLayout notifyTimeLayout = (TextInputLayout) binding.notifyTimeEdit.getParent().getParent();

        // Create a single click listener for showing the date-time picker
        View.OnClickListener dateTimePickerClickListener = v -> {
            LocalDateTime initialDateTime = LocalDateTime.now();
            String currentText = binding.notifyTimeEdit.getText().toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (!TextUtils.isEmpty(currentText)) {
                try {
                    initialDateTime = LocalDateTime.parse(currentText, formatter);
                } catch (Exception e) {
                    // Ignore parse error, use now()
                }
            }
            DatePickerUtils.showDateTimePickerDialog(
                    requireContext(),
                    initialDateTime,
                    selectedDateTime -> binding.notifyTimeEdit.setText(selectedDateTime.format(formatter))
            );
        };

        // Set the listener on both the EditText and the end icon
        binding.notifyTimeEdit.setOnClickListener(dateTimePickerClickListener);
        notifyTimeLayout.setEndIconOnClickListener(dateTimePickerClickListener);

        // Setup for time-only pickers
        binding.startTimeEdit.setOnClickListener(v -> showTimePickerDialog(binding.startTimeEdit));
        binding.endTimeEdit.setOnClickListener(v -> showTimePickerDialog(binding.endTimeEdit));
    }

    private void showTimePickerDialog(TextInputEditText timeEditText) {
        LocalTime initialTime = LocalTime.now();
        String currentText = timeEditText.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        if (!TextUtils.isEmpty(currentText)) {
            try {
                initialTime = LocalTime.parse(currentText, formatter);
            } catch (Exception e) {
                // Ignore parse error, use now()
            }
        }

        DatePickerUtils.showTimePickerDialog(
                requireContext(),
                initialTime,
                selectedTime -> timeEditText.setText(selectedTime.format(formatter))
        );
    }
}
