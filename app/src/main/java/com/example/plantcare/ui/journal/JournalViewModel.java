package com.example.plantcare.ui.journal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.Plant;
import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.data.repository.JournalRepository;
import com.example.plantcare.ui.base.BaseViewModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class JournalViewModel extends BaseViewModel {

    private final JournalRepository journalRepository;
    private final PlantDao plantDao;
    private final Executor executor = Executors.newSingleThreadExecutor();


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
        executor.execute(() -> {
            journalRepository.deleteAllJournalsForPlant(plantId);
            _toastMessage.postValue("Đã xóa tất cả nhật ký cho cây");
        });
    }

    public void deleteJournal(Journal journal) {
        executor.execute(() -> {
            journalRepository.delete(journal);
            _toastMessage.postValue("Đã xóa nhật ký");
        });
    }
}
