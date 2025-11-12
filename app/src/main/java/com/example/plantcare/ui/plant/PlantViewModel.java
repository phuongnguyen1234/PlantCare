package com.example.plantcare.ui.plant;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.PlantRepository;

import java.util.List;

public class PlantViewModel extends AndroidViewModel {
    private final PlantRepository plantRepository;
    private final LiveData<List<Plant>> allPlants;

    public PlantViewModel(@NonNull Application application) {
        super(application);
        plantRepository = new PlantRepository(application);
        allPlants = plantRepository.getAllPlants();
    }

    public LiveData<List<Plant>> getAllPlants() {
        return allPlants;
    }

    public void delete(Plant plant) {
        plantRepository.delete(plant);
    }
}
