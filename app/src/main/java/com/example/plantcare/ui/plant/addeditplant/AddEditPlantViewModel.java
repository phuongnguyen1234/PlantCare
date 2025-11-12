package com.example.plantcare.ui.plant.addeditplant;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.PlantRepository;

public class AddEditPlantViewModel extends AndroidViewModel {
    private final PlantRepository plantRepository;
    private final MutableLiveData<Uri> plantImageUri = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _saveComplete = new MutableLiveData<>();
    public final LiveData<Boolean> saveComplete = _saveComplete;

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
        if (plant.getPlantId() == 0) {
            plantRepository.insert(plant);
        } else {
            plantRepository.update(plant);
        }
        _saveComplete.setValue(true);
    }
    
    public void onSaveComplete() {
        _saveComplete.setValue(false);
    }
}
