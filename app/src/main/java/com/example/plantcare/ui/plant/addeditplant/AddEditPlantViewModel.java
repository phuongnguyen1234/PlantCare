package com.example.plantcare.ui.plant.addeditplant;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.PlantRepository;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddEditPlantViewModel extends AndroidViewModel {
    private final PlantRepository plantRepository;
    private final MutableLiveData<Uri> plantImageUri = new MutableLiveData<>();

    private final MutableLiveData<Boolean> _saveComplete = new MutableLiveData<>();
    public final LiveData<Boolean> saveComplete = _saveComplete;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public final LiveData<String> toastMessage = _toastMessage;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public AddEditPlantViewModel(@NonNull Application application) {
        super(application);
        plantRepository = new PlantRepository(application);
    }

    public LiveData<Uri> getPlantImageUri() {
        return plantImageUri;
    }

    public void setPlantImageUri(Uri uri) {
        plantImageUri.setValue(uri);
    }

    public LiveData<Plant> getPlantById(int plantId) {
        return plantRepository.getPlantById(plantId);
    }

    public void savePlant(Plant plant) {
        executor.execute(() -> {
            try {
                Uri imageUri = plantImageUri.getValue();
                if (imageUri != null) {
                    String imagePath = plantRepository.copyImageToInternalStorage(imageUri);
                    plant.setImageUrl(imagePath);
                }

                boolean isNewPlant = plant.getPlantId() == 0;
                if (isNewPlant) {
                    plantRepository.insert(plant);
                    _toastMessage.postValue("Thêm cây thành công");
                } else {
                    plantRepository.update(plant);
                    _toastMessage.postValue("Cập nhật cây thành công");
                }
                _saveComplete.postValue(true);
            } catch (IOException e) {
                Log.e("AddEditPlantVM", "Failed to copy image", e);
                _toastMessage.postValue("Không thể sao chép ảnh");
            } catch (Exception e) {
                Log.e("AddEditPlantVM", "Error saving plant", e);
                _toastMessage.postValue("Lưu cây thất bại");
            }
        });
    }

    public void onSaveComplete() {
        _saveComplete.setValue(false);
    }

    public void onToastMessageShown() {
        _toastMessage.setValue(null);
    }
}
