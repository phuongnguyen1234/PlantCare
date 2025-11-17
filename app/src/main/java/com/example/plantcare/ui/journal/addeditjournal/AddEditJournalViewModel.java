package com.example.plantcare.ui.journal.addeditjournal;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.data.model.JournalWithImages;
import com.example.plantcare.data.repository.JournalRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddEditJournalViewModel extends AndroidViewModel {
    private final JournalRepository repository;
    private final MutableLiveData<String> content = new MutableLiveData<>();
    private final MutableLiveData<String> dateText = new MutableLiveData<>();
    private final MutableLiveData<List<String>> imageUrls = new MutableLiveData<>(new ArrayList<>());

    public AddEditJournalViewModel(@NonNull Application application) {
        super(application);
        repository = new JournalRepository(application);
    }

    // Method for the Fragment to get the Journal with its images
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
        Journal journal = new Journal();
        journal.setPlantId(plantId);
        journal.setPlantName(plantName);
        journal.setContent(content);
        journal.setDateCreated(LocalDateTime.now());
        repository.saveJournalWithImages(journal, images);
    }

    public void updateJournal(Journal journal, String content, List<String> images) {
        journal.setContent(content);
        repository.updateJournalWithImages(journal, images);
    }

    public void updateDateText(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateText.setValue(dateTime.format(formatter));
    }
}
