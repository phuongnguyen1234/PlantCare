package com.example.plantcare.ui.journal.addeditjournal;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.data.repository.JournalRepository;
import com.example.plantcare.ui.base.BaseViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddEditJournalViewModel extends BaseViewModel {
    private static final String TAG = "AddEditJournalVM";
    private final JournalRepository repository;
    private final MutableLiveData<String> content = new MutableLiveData<>();
    private final MutableLiveData<String> dateText = new MutableLiveData<>();
    private final MutableLiveData<List<String>> imageUrls = new MutableLiveData<>(new ArrayList<>());
    private final Executor executor = Executors.newSingleThreadExecutor();

    public AddEditJournalViewModel(@NonNull Application application) {
        super(application);
        repository = new JournalRepository(application);
    }

    public LiveData<JournalWithImages> getJournalWithImages(int journalId) {
        return repository.getJournalWithImagesById(journalId);
    }

    public LiveData<String> getContent() {
        return content;
    }

    public LiveData<String> getDateText() {
        return dateText;
    }

    public LiveData<List<String>> getImageUrls() {
        return imageUrls;
    }

    public void setContent(String text) {
        content.setValue(text);
    }

    public void setImageUrls(List<JournalImage> images) {
        if (images != null) {
            List<String> urls = images.stream().map(JournalImage::getImageUrl).collect(Collectors.toList());
            imageUrls.setValue(urls);
        } else {
            imageUrls.setValue(new ArrayList<>());
        }
    }

    public void addImageUri(String uri) {
        List<String> currentUris = imageUrls.getValue();
        if (currentUris != null && currentUris.size() < 6) {
            currentUris.add(uri);
            imageUrls.setValue(new ArrayList<>(currentUris)); // Trigger observer with a new list
        }
    }

    public void removeImageUri(int position) {
        List<String> currentUris = imageUrls.getValue();
        if (currentUris != null && position >= 0 && position < currentUris.size()) {
            currentUris.remove(position);
            imageUrls.setValue(new ArrayList<>(currentUris)); // Trigger observer with a new list
        }
    }

    public void prepareNewJournalDate() {
        updateDateText(LocalDateTime.now());
    }

    public void saveJournal(int plantId, String plantName, String content, List<String> images) {
        executor.execute(() -> {
            try {
                Journal journal = new Journal();
                journal.setPlantId(plantId);
                journal.setPlantName(plantName);
                journal.setContent(content);
                journal.setDateCreated(LocalDateTime.now());
                repository.saveJournalWithImages(journal, images);
                _toastMessage.postValue("Đã thêm nhật ký");
                _navigateBack.postValue(true);
            } catch (Exception e) {
                Log.e(TAG, "Error saving journal", e);
                _toastMessage.postValue("Lưu nhật ký thất bại");
            }
        });
    }

    public void updateJournal(Journal journal, String content, List<String> images) {
        executor.execute(() -> {
            try {
                journal.setContent(content);
                repository.updateJournalWithImages(journal, images);
                _toastMessage.postValue("Đã cập nhật nhật ký");
                _navigateBack.postValue(true);
            } catch (Exception e) {
                Log.e(TAG, "Error updating journal", e);
                _toastMessage.postValue("Cập nhật nhật ký thất bại");
            }
        });
    }

    public void updateDateText(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateText.setValue(dateTime.format(formatter));
    }
}
