package com.example.plantcare.ui.plant;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.PlantRepository;

import java.util.List;

public class PlantViewModel extends ViewModel {
    private final PlantRepository plantRepository;
    private final LiveData<List<Plant>> allPlants;

    public PlantViewModel() {
        plantRepository = new PlantRepository();
        allPlants = plantRepository.getAllPlants();
    }

    public LiveData<List<Plant>> getAllPlants() {
        return allPlants;
    }
}
