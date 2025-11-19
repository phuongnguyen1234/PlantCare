package com.example.plantcare.ui.plant.addeditplant;

import android.net.Uri;
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
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.databinding.FragmentAddEditPlantBinding;
import com.example.plantcare.ui.main.BaseFragment;
import com.example.plantcare.utils.DatePickerUtils;
import com.example.plantcare.utils.DropdownUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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

        setupUI();
        observeViewModel();

        if (plantId != -1) {
            mViewModel.getPlantById(plantId).observe(getViewLifecycleOwner(), plant -> {
                if (plant != null) {
                    currentPlant = plant;
                    mViewModel.setOriginalImageUrl(plant.getImageUrl()); // Important: Set original image URL
                    binding.setPlant(currentPlant);
                    updateUIWithPlantData(currentPlant);
                }
            });
        } else {
            currentPlant = new Plant();
            mViewModel.setOriginalImageUrl(null); // Important: No original image for new plant
            binding.setPlant(currentPlant);
            binding.etPlantingDate.setText(LocalDate.now().format(dateFormatter));
        }
    }

    private void setupUI() {
        binding.btnSave.setOnClickListener(v -> {
            if (isValidInput()) {
                updatePlantFromFields();
                mViewModel.savePlant(currentPlant);
            }
        });

        View.OnClickListener datePickerClickListener = v -> {
            LocalDate initialDate = LocalDate.now();
            String currentText = binding.etPlantingDate.getText().toString();
            if (!TextUtils.isEmpty(currentText)) {
                try {
                    initialDate = LocalDate.parse(currentText, dateFormatter);
                } catch (Exception e) {
                    // Ignore parse error, use today's date
                }
            }
            DatePickerUtils.showDatePickerDialog(
                    requireContext(),
                    initialDate,
                    selectedDate -> binding.etPlantingDate.setText(selectedDate.format(dateFormatter))
            );
        };

        binding.etPlantingDate.setOnClickListener(datePickerClickListener);
        binding.plantingDateLayout.setEndIconOnClickListener(datePickerClickListener);

        binding.ivPlantImage.setOnClickListener(v -> {
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        binding.ivDeleteImage.setOnClickListener(v -> {
            mViewModel.setPlantImageUri(null);
        });

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

        mViewModel.toastMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                mViewModel.onToastMessageShown();
            }
        });
    }

    private void updateUIWithPlantData(Plant plant) {
        // The ViewModel now manages the image Uri, this fragment just observes
        if (plant.getDatePlanted() != null) {
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

        try {
            currentPlant.setDatePlanted(LocalDate.parse(binding.etPlantingDate.getText().toString(), dateFormatter));
        } catch (Exception e) {
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

        currentPlant.setWaterUnit(DropdownUtils.getEnumValueFromDisplayName(FrequencyUnit.class, binding.actvWaterUnit.getText().toString()));
        currentPlant.setFertilizerUnit(DropdownUtils.getEnumValueFromDisplayName(FrequencyUnit.class, binding.actvFertilizerUnit.getText().toString()));
        currentPlant.setLightUnit(DropdownUtils.getEnumValueFromDisplayName(FrequencyUnit.class, binding.actvLightUnit.getText().toString()));

        currentPlant.setNote(binding.etNote.getText().toString());
    }
}
