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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AddEditPlantViewModel extends AndroidViewModel {
    private final PlantRepository plantRepository;
    private final MutableLiveData<Uri> plantImageUri = new MutableLiveData<>();

    private final MutableLiveData<Boolean> _saveComplete = new MutableLiveData<>();
    public final LiveData<Boolean> saveComplete = _saveComplete;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public final LiveData<String> toastMessage = _toastMessage;

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

    /**
     * Copies an image from a content URI to the app's internal storage.
     * @param contentUri The URI of the image to copy.
     * @return The URI of the newly created file in internal storage, or null on failure.
     */
    public Uri copyImageToInternalStorage(Uri contentUri) {
        Application application = getApplication();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        File newFile;
        try {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String fileName = "plant_" + timeStamp + ".jpg";

            File imageDir = new File(application.getFilesDir(), "images/plant");
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            newFile = new File(imageDir, fileName);

            inputStream = application.getContentResolver().openInputStream(contentUri);
            if (inputStream == null) {
                throw new IOException("Unable to open input stream for " + contentUri);
            }
            outputStream = new FileOutputStream(newFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            return Uri.fromFile(newFile);

        } catch (IOException e) {
            Log.e("AddEditPlantVM", "Failed to copy image", e);
            _toastMessage.postValue("Không thể sao chép ảnh");
            return null; // Failure
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                Log.e("AddEditPlantVM", "Error closing streams", e);
            }
        }
    }

    public LiveData<Plant> getPlantById(int plantId) {
        return plantRepository.getPlantById(plantId);
    }

    public void savePlant(Plant plant) {
        try {
            boolean isNewPlant = plant.getPlantId() == 0;
            if (isNewPlant) {
                plantRepository.insert(plant);
                _toastMessage.setValue("Thêm cây thành công");
            } else {
                plantRepository.update(plant);
                _toastMessage.setValue("Cập nhật cây thành công");
            }
            _saveComplete.setValue(true);
        } catch (Exception e) {
            Log.e("AddEditPlantVM", "Error saving plant", e);
            _toastMessage.setValue("Lưu cây thất bại");
        }
    }

    public void onSaveComplete() {
        _saveComplete.setValue(false);
    }

    public void onToastMessageShown() {
        _toastMessage.setValue(null);
    }
}
