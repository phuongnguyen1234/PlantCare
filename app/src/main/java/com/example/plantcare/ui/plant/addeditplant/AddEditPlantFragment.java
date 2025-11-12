package com.example.plantcare.ui.plant.addeditplant;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.example.plantcare.R;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.enums.DisplayableEnum;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.databinding.FragmentAddEditPlantBinding;
import com.example.plantcare.ui.main.BaseFragment;
import com.example.plantcare.utils.DropdownUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AddEditPlantFragment extends BaseFragment<FragmentAddEditPlantBinding> {

    private AddEditPlantViewModel mViewModel;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;
    private int plantId = -1; // Default to -1 for add mode
    private Plant currentPlant;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            plantId = getArguments().getInt("plantId", -1);
        }

        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                mViewModel.setPlantImageUri(uri);
                if (currentPlant != null) {
                    currentPlant.setImageUrl(uri.toString());
                }
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_add_edit_plant;
    }

    @Override
    protected String getToolbarTitle() {
        return plantId == -1 ? "Thêm cây mới" : "Sửa cây";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(AddEditPlantViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // 1. SETUP UI FIRST: Populate dropdowns and prepare UI elements.
        setupUI();
        observeViewModel();

        // 2. LOAD DATA: Now that the UI is ready, load the data.
        if (plantId != -1) {
            mViewModel.getPlantById(plantId).observe(getViewLifecycleOwner(), plant -> {
                if (plant != null) {
                    currentPlant = plant;
                    binding.setPlant(currentPlant);
                    // This will correctly select the pre-filled value in the dropdown.
                    updateUIWithPlantData(currentPlant);
                }
            });
        } else {
            currentPlant = new Plant();
            binding.setPlant(currentPlant);
        }
    }

    private void setupUI() {
        binding.btnSave.setOnClickListener(v -> {
            if (isValidInput()) {
                updatePlantFromFields();
                mViewModel.savePlant(currentPlant);
            }
        });

        binding.etPlantingDate.setOnClickListener(v -> showDatePickerDialog());

        binding.ivPlantImage.setOnClickListener(v -> {
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // This populates the dropdowns with ALL possible values.
        DropdownUtils.setupEnumDropdown(binding.actvWaterUnit, FrequencyUnit.class);
        DropdownUtils.setupEnumDropdown(binding.actvFertilizerUnit, FrequencyUnit.class);
        DropdownUtils.setupEnumDropdown(binding.actvLightUnit, FrequencyUnit.class);
    }

    private void observeViewModel() {
        mViewModel.saveComplete.observe(getViewLifecycleOwner(), isSaveComplete -> {
            if (isSaveComplete != null && isSaveComplete) {
                getParentFragmentManager().popBackStack();
                mViewModel.onSaveComplete();
            }
        });
    }

    private void showDatePickerDialog() {
        LocalDate initialDate = LocalDate.now();
        try {
            // Try to parse the existing date to pre-set the dialog
            initialDate = LocalDate.parse(binding.etPlantingDate.getText().toString(), dateFormatter);
        } catch (Exception e) {
            // Ignore and use today's date if parsing fails
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, monthOfYear, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
                    binding.etPlantingDate.setText(selectedDate.format(dateFormatter));
                }, initialDate.getYear(), initialDate.getMonthValue() - 1, initialDate.getDayOfMonth());

        datePickerDialog.show();
    }

    private void updateUIWithPlantData(Plant plant) {
        // Set display name for dropdowns. The adapter is already set.
        if (plant.getDatePlanted() != null) {
            // Use the consistent formatter to display the date
            binding.etPlantingDate.setText(plant.getDatePlanted().format(dateFormatter));
        }

        if (plant.getWaterUnit() != null) {
            binding.actvWaterUnit.setText(plant.getWaterUnit().getDisplayName(), false);
        }
        if (plant.getFertilizerUnit() != null) {
            binding.actvFertilizerUnit.setText(plant.getFertilizerUnit().getDisplayName(), false);
        }
        if (plant.getLightUnit() != null) {
            binding.actvLightUnit.setText(plant.getLightUnit().getDisplayName(), false);
        }

        // Split and set range fields
        if (plant.getTemperatureRange() != null && !plant.getTemperatureRange().isEmpty()) {
            String[] tempRange = plant.getTemperatureRange().split("-");
            if (tempRange.length == 2) {
                binding.etTempMin.setText(tempRange[0]);
                binding.etTempMax.setText(tempRange[1]);
            }
        }

        if (plant.getHumidityRange() != null && !plant.getHumidityRange().isEmpty()) {
            String[] humidityRange = plant.getHumidityRange().split("-");
            if (humidityRange.length == 2) {
                binding.etHumidityMin.setText(humidityRange[0]);
                binding.etHumidityMax.setText(humidityRange[1]);
            }
        }
    }

    private boolean isValidInput() {
        // Clear previous errors
        binding.etPlantName.setError(null);
        binding.etWaterFrequency.setError(null);
        binding.actvWaterUnit.setError(null);
        binding.etPlantingDate.setError(null);

        if (TextUtils.isEmpty(binding.etPlantingDate.getText())) {
            binding.etPlantingDate.setError("Vui lòng chọn ngày trồng");
            return false;
        }
        if (TextUtils.isEmpty(binding.etPlantName.getText())) {
            binding.etPlantName.setError("Vui lòng nhập tên cây");
            return false;
        }
        if (TextUtils.isEmpty(binding.etWaterFrequency.getText())) {
            binding.etWaterFrequency.setError("Vui lòng nhập tần suất");
            return false;
        }
        if (TextUtils.isEmpty(binding.actvWaterUnit.getText())) {
            binding.actvWaterUnit.setError("Vui lòng chọn đơn vị");
            return false;
        }
        return true;
    }

    private void updatePlantFromFields() {
        currentPlant.setName(binding.etPlantName.getText().toString());

        // Use the consistent formatter to parse the date
        try {
            currentPlant.setDatePlanted(LocalDate.parse(binding.etPlantingDate.getText().toString(), dateFormatter));
        } catch(Exception e) {
            // Set to now as a fallback, though validation should prevent this
            currentPlant.setDatePlanted(LocalDate.now());
        }

        String tempMin = binding.etTempMin.getText().toString();
        String tempMax = binding.etTempMax.getText().toString();
        if (!tempMin.isEmpty() && !tempMax.isEmpty()) {
            currentPlant.setTemperatureRange(tempMin + "-" + tempMax);
        } else {
            currentPlant.setTemperatureRange(null);
        }

        String humidityMin = binding.etHumidityMin.getText().toString();
        String humidityMax = binding.etHumidityMax.getText().toString();
        if (!humidityMin.isEmpty() && !humidityMax.isEmpty()) {
            currentPlant.setHumidityRange(humidityMin + "-" + humidityMax);
        } else {
            currentPlant.setHumidityRange(null);
        }

        try {
            currentPlant.setWaterFrequency(Integer.parseInt(binding.etWaterFrequency.getText().toString()));
        } catch (NumberFormatException e) {
            currentPlant.setWaterFrequency(0);
        }

        try {
            String fertilizerFreq = binding.etFertilizerFrequency.getText().toString();
            currentPlant.setFertilizerFrequency(!TextUtils.isEmpty(fertilizerFreq) ? Integer.parseInt(fertilizerFreq) : 0);
        } catch (NumberFormatException e) {
            currentPlant.setFertilizerFrequency(0);
        }

        try {
            String lightFreq = binding.etLightFrequency.getText().toString();
            currentPlant.setLightFrequency(!TextUtils.isEmpty(lightFreq) ? Integer.parseInt(lightFreq) : 0);
        } catch (NumberFormatException e) {
            currentPlant.setLightFrequency(0);
        }

        // Convert display name back to enum value before saving
        currentPlant.setWaterUnit(getEnumValueFromDisplayName(FrequencyUnit.class, binding.actvWaterUnit.getText().toString()));
        currentPlant.setFertilizerUnit(getEnumValueFromDisplayName(FrequencyUnit.class, binding.actvFertilizerUnit.getText().toString()));
        currentPlant.setLightUnit(getEnumValueFromDisplayName(FrequencyUnit.class, binding.actvLightUnit.getText().toString()));

        currentPlant.setNote(binding.etNote.getText().toString());
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
}
