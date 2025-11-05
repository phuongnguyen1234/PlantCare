package com.example.plantcare.data.repository;

import android.content.Context;

import androidx.room.Room;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.entity.Plant;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlantRepository {
    private final PlantDao plantDao;
    private final ExecutorService executorService;

    public PlantRepository(Context context) {
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "plantcare.db").build();
        plantDao = db.plantDao();
        executorService = Executors.newSingleThreadExecutor();
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

    public List<Plant> getAllPlants() {
        return plantDao.getAllPlants();
    }

    public Plant getPlantById(int id) {
        return plantDao.getPlantById(id);
    }
}
