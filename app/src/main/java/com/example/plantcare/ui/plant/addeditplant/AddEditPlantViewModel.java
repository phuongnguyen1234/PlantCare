package com.example.plantcare.ui.plant.addeditplant;

import android.app.Application;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.PlantRepository;
import com.example.plantcare.ui.base.BaseViewModel;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddEditPlantViewModel extends BaseViewModel {
    private static final String TAG = "AddEditPlantVM";
    private final PlantRepository plantRepository;
    private final MutableLiveData<Uri> plantImageUri = new MutableLiveData<>();
    private String originalImageUrl; // To track the initial image state
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

    public void setOriginalImageUrl(String url) {
        this.originalImageUrl = url;
        if (!TextUtils.isEmpty(url)) {
            // Also set the initial preview image
            plantImageUri.setValue(Uri.parse(url));
        } else {
            plantImageUri.setValue(null);
        }
    }

    public LiveData<Plant> getPlantById(int plantId) {
        return plantRepository.getPlantById(plantId);
    }

    public void savePlant(Plant plant) {
        executor.execute(() -> {
            try {
                Uri newImageUri = plantImageUri.getValue();

                // Determine if the image has changed. A new image will have a "content://" scheme.
                boolean isNewImage = newImageUri != null && "content".equals(newImageUri.getScheme());
                // Determine if the image was cleared.
                boolean imageCleared = newImageUri == null && !TextUtils.isEmpty(originalImageUrl);

                if (isNewImage) {
                    // 1. A new image was selected from the gallery.
                    if (!TextUtils.isEmpty(originalImageUrl)) {
                        plantRepository.deleteImageFile(originalImageUrl);
                    }
                    // Copy the new image and get its path.
                    String newImagePath = plantRepository.copyImageToInternalStorage(newImageUri);
                    plant.setImageUrl(newImagePath);
                } else if (imageCleared) {
                    // 2. The existing image was deleted by the user.
                    plantRepository.deleteImageFile(originalImageUrl);
                    plant.setImageUrl(null);
                }
                // 3. If neither of the above, the image was not changed.

                boolean isNewPlant = plant.getPlantId() == 0;
                if (isNewPlant) {
                    plantRepository.insert(plant);
                    _toastMessage.postValue("Thêm cây thành công");
                } else {
                    plantRepository.update(plant);
                    _toastMessage.postValue("Cập nhật cây thành công");
                }
                _navigateBack.postValue(true);
            } catch (IOException e) {
                Log.e(TAG, "Failed to copy image", e);
                _toastMessage.postValue("Không thể sao chép ảnh");
            } catch (Exception e) {
                Log.e(TAG, "Error saving plant", e);
                _toastMessage.postValue("Lưu cây thất bại");
            }
        });
    }
}
