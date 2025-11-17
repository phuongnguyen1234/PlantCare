package com.example.plantcare.data.repository;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.dao.JournalDao;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.data.model.JournalWithImages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class JournalRepository {
    private static final String TAG = "JournalRepository";
    private final JournalDao journalDao;
    private final ExecutorService executorService;
    private final Application application;

    public JournalRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.application = application;
        journalDao = db.journalDao();
        executorService = AppDatabase.databaseWriteExecutor;
    }

    private void deleteImageFile(String path) {
        if (path != null && !path.isEmpty()) {
            File file = new File(path);
            if (file.exists()) {
                if (!file.delete()) {
                    Log.w(TAG, "Failed to delete image file: " + path);
                }
            }
        }
    }

    private String copyImageToInternalStorage(String uriString) {
        if (uriString == null) {
            return null;
        }

        if (uriString.startsWith(application.getFilesDir().getAbsolutePath())) {
            return uriString;
        }

        try {
            Uri uri = Uri.parse(uriString);
            ContentResolver resolver = application.getContentResolver();
            InputStream inputStream = resolver.openInputStream(uri);

            if (inputStream == null) {
                return null;
            }

            File targetDir = new File(application.getFilesDir(), "images/journal");
            if (!targetDir.exists()) {
                if (!targetDir.mkdirs()) {
                    Log.w(TAG, "Failed to create directory: " + targetDir.getAbsolutePath());
                    return null;
                }
            }

            String fileName = "IMG_" + System.currentTimeMillis() + ".jpg";
            File targetFile = new File(targetDir, fileName);

            FileOutputStream outputStream = new FileOutputStream(targetFile);

            byte[] buffer = new byte[4 * 1024]; // 4k buffer
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }

            outputStream.flush();
            outputStream.close();
            inputStream.close();

            return targetFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error copying image to internal storage", e);
            return null;
        }
    }

    public void saveJournalWithImages(Journal journal, List<String> imageUrls) {
        executorService.execute(() -> {
            long journalId = journalDao.insert(journal);

            if (imageUrls != null && !imageUrls.isEmpty()) {
                List<JournalImage> journalImages = imageUrls.stream()
                        .map(this::copyImageToInternalStorage)
                        .filter(Objects::nonNull)
                        .map(path -> new JournalImage((int) journalId, path))
                        .collect(Collectors.toList());
                if (!journalImages.isEmpty()) {
                    journalDao.insertImages(journalImages);
                }
            }
        });
    }

    public void updateJournalWithImages(Journal journal, List<String> newImageUrls) {
        executorService.execute(() -> {
            List<String> oldImagePaths = journalDao.getImagesByJournalId(journal.getJournalId())
                    .stream()
                    .map(JournalImage::getImageUrl)
                    .collect(Collectors.toList());

            List<String> finalNewImageUrls = newImageUrls != null ? newImageUrls : Collections.emptyList();

            oldImagePaths.stream()
                    .filter(path -> !finalNewImageUrls.contains(path))
                    .forEach(this::deleteImageFile);

            journalDao.update(journal);
            journalDao.deleteImagesByJournalId(journal.getJournalId());

            if (newImageUrls != null && !newImageUrls.isEmpty()) {
                List<JournalImage> journalImages = newImageUrls.stream()
                        .map(this::copyImageToInternalStorage)
                        .filter(Objects::nonNull)
                        .map(path -> new JournalImage(journal.getJournalId(), path))
                        .collect(Collectors.toList());

                if (!journalImages.isEmpty()) {
                    journalDao.insertImages(journalImages);
                }
            }
        });
    }

    public void delete(Journal journal) {
        executorService.execute(() -> {
            journalDao.getImagesByJournalId(journal.getJournalId())
                    .stream()
                    .map(JournalImage::getImageUrl)
                    .forEach(this::deleteImageFile);
            journalDao.delete(journal);
        });
    }

    public void deleteAllJournalsForPlant(int plantId) {
        executorService.execute(() -> {
            journalDao.getAllImagesForPlantSync(plantId)
                    .stream()
                    .map(JournalImage::getImageUrl)
                    .forEach(this::deleteImageFile);
            journalDao.deleteAllJournalsByPlantId(plantId);
            journalDao.deleteAllImagesForPlant(plantId);
        });
    }

    public LiveData<List<JournalWithImages>> getJournalsByPlantId(int plantId) {
        return journalDao.getJournalsWithImagesByPlantId(plantId);
    }

    public LiveData<JournalWithImages> getJournalWithImagesById(int journalId) {
        return journalDao.getJournalWithImagesById(journalId);
    }

    public LiveData<List<JournalWithImages>> getLatestJournalForEachPlant() {
        return journalDao.getLatestJournalForEachPlant();
    }
}
