package com.example.plantcare.ui.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.repository.JournalRepository;
import com.example.plantcare.data.repository.PlantRepository;

import java.util.List;

public class JournalViewModel extends AndroidViewModel {

    private final JournalRepository journalRepo;
    private final PlantRepository plantRepo;

    public JournalViewModel(@NonNull Application application) {
        super(application);
        journalRepo = new JournalRepository(application);
        plantRepo = new PlantRepository(application);
    }

    public LiveData<List<Journal>> getJournalsByPlantId(int plantId) {
        return journalRepo.getJournalsByPlantId(plantId);
    }

    public LiveData<List<Plant>> getAllPlants() {
        return plantRepo.getAllPlants();
    }
}
