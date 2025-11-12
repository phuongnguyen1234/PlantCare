package com.example.plantcare.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.entity.Plant;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class PlantRepository {
    private final PlantDao plantDao;
    private final ExecutorService executorService;

    public PlantRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        plantDao = db.plantDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    public void insert(Plant plant) {
        executorService.execute(() -> plantDao.insert(plant));
    }

    public void update(Plant plant) {
        executorService.execute(() -> plantDao.update(plant));
    }

    public void delete(Plant plant) {
        executorService.execute(() -> plantDao.delete(plant));
    }

    public LiveData<List<Plant>> getAllPlants() {
        return plantDao.getAllPlants();
    }

    public LiveData<Plant> getPlantById(int id) {
        return plantDao.getPlantById(id);
    }
}
