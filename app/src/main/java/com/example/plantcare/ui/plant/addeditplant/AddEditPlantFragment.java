package com.example.plantcare.ui.plant.addeditplant;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.plantcare.R;
import com.example.plantcare.data.enums.FrequencyUnit;
import com.example.plantcare.databinding.FragmentAddEditPlantBinding;
import com.example.plantcare.ui.main.BaseFragment;
import com.example.plantcare.utils.DropdownUtils;

import java.util.Calendar;
import java.util.Locale;

public class AddEditPlantFragment extends BaseFragment<FragmentAddEditPlantBinding> {

    private AddEditPlantViewModel mViewModel;
    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register the ActivityResultLauncher for the photo picker.
        // This should be done in onCreate or onAttach.
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                // The user picked a photo. Load it into the ImageView using Glide.
                if (binding != null) {
                    binding.ivPlantImage.setPadding(0, 0, 0, 0);
                    binding.ivPlantImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    Glide.with(this)
                            .load(uri)
                            .into(binding.ivPlantImage);
                }
                // TODO: Save the URI to the ViewModel for persistence
                // mViewModel.setImageUri(uri.toString());
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_add_edit_plant;
    }

    @Override
    protected String getToolbarTitle() {
        // TODO: Logic for Add/Edit mode
        return "Thêm cây mới";
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(AddEditPlantViewModel.class);
        binding.setViewModel(mViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        // TODO: Logic for Add/Edit mode

        setupUI();
    }

    private void setupUI() {
        // Setup event listeners
        binding.btnSave.setOnClickListener(v -> {
            // mViewModel.savePlant();
        });
        binding.etPlantingDate.setOnClickListener(v -> showDatePickerDialog());

        binding.ivPlantImage.setOnClickListener(v -> {
            // Launch the modern photo picker
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Setup dropdowns
        DropdownUtils.setupEnumDropdown(binding.actvWaterUnit, FrequencyUnit.class);
        DropdownUtils.setupEnumDropdown(binding.actvFertilizerUnit, FrequencyUnit.class);
        DropdownUtils.setupEnumDropdown(binding.actvLightUnit, FrequencyUnit.class);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    binding.etPlantingDate.setText(selectedDate);
                    // TODO: Save this date to the ViewModel
                }, year, month, day);

        datePickerDialog.show();
    }
}
