package com.example.plantcare.ui.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalDao;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.data.repository.JournalRepository;

import java.util.List;

public class JournalViewModel extends AndroidViewModel {

    private final JournalRepository journalRepository;
    private final PlantDao plantDao;

    public JournalViewModel(@NonNull Application application) {
        super(application);
        journalRepository = new JournalRepository(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        plantDao = db.plantDao();
    }

    public LiveData<List<JournalWithImages>> getLatestJournalForEachPlant() {
        return journalRepository.getLatestJournalForEachPlant();
    }

    public LiveData<List<Plant>> getAllPlants() {
        return plantDao.getAllPlants();
    }

    public LiveData<Plant> getPlantById(int plantId) {
        return plantDao.getPlantById(plantId);
    }

    public LiveData<List<JournalWithImages>> getJournalsWithImagesByPlantId(int plantId) {
        return journalRepository.getJournalsByPlantId(plantId);
    }

    public void deleteAllJournalsForPlant(int plantId) {
        journalRepository.deleteAllJournalsForPlant(plantId);
    }

    public void deleteJournal(Journal journal) {
        journalRepository.delete(journal);
    }
}
