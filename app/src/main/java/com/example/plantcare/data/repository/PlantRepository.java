package com.example.plantcare.data.repository;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.entity.Plant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class PlantRepository {
    private static final String TAG = "PlantRepository";
    private final PlantDao plantDao;
    private final ExecutorService executorService;
    private final Application application;

    public PlantRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        plantDao = db.plantDao();
        executorService = AppDatabase.databaseWriteExecutor;
        this.application = application;
    }

    public String copyImageToInternalStorage(Uri contentUri) throws IOException {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            String timeStamp = String.valueOf(System.currentTimeMillis());
            String fileName = "plant_" + timeStamp + ".jpg";

            File imageDir = new File(application.getFilesDir(), "images/plant");
            if (!imageDir.exists()) {
                if (!imageDir.mkdirs()) {
                    Log.w(TAG, "Failed to create image directory: " + imageDir.getAbsolutePath());
                    throw new IOException("Failed to create image directory");
                }
            }

            File newFile = new File(imageDir, fileName);

            ContentResolver resolver = application.getContentResolver();
            inputStream = resolver.openInputStream(contentUri);
            if (inputStream == null) {
                throw new IOException("Unable to open input stream for " + contentUri);
            }
            outputStream = new FileOutputStream(newFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            return newFile.getAbsolutePath();

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing input stream", e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error closing output stream", e);
                }
            }
        }
    }

    public void deleteImageFile(String path) {
        if (path != null && !path.isEmpty()) {
            File imageFile = new File(path);
            if (imageFile.exists()) {
                if (!imageFile.delete()) {
                    Log.w(TAG, "Failed to delete image file: " + path);
                }
            }
        }
    }

    public void insert(Plant plant) {
        executorService.execute(() -> plantDao.insert(plant));
    }

    public void update(Plant plant) {
        executorService.execute(() -> plantDao.update(plant));
    }

    public void delete(Plant plant) {
        executorService.execute(() -> {
            deleteImageFile(plant.getImageUrl());
            plantDao.delete(plant);
        });
    }

    public LiveData<List<Plant>> getAllPlants() {
        return plantDao.getAllPlants();
    }

    public LiveData<Plant> getPlantById(int id) {
        return plantDao.getPlantById(id);
    }
}
