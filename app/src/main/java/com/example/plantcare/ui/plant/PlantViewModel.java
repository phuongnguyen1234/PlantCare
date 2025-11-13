package com.example.plantcare.ui.plant;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.PlantRepository;

import java.util.List;

public class PlantViewModel extends AndroidViewModel {
    private final PlantRepository plantRepository;
    private final LiveData<List<Plant>> allPlants;

    private final MutableLiveData<String> _toastMessage = new MutableLiveData<>();
    public final LiveData<String> toastMessage = _toastMessage;

    public PlantViewModel(@NonNull Application application) {
        super(application);
        plantRepository = new PlantRepository(application);
        allPlants = plantRepository.getAllPlants();
    }

    public LiveData<List<Plant>> getAllPlants() {
        return allPlants;
    }

    public void delete(Plant plant) {
        try {
            plantRepository.delete(plant);
            _toastMessage.setValue("Đã xóa: " + plant.getName());
        } catch (Exception e) {
            Log.e("PlantViewModel", "Error deleting plant", e);
            _toastMessage.setValue("Xóa cây thất bại");
        }
    }

    public void onToastMessageShown() {
        _toastMessage.setValue(null);
    }
}
