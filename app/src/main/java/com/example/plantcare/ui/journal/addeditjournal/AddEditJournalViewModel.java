package com.example.plantcare.ui.journal.addeditjournal;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.plantcare.data.AppDatabase;
import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;
import com.example.plantcare.data.repository.JournalImageRepository;
import com.example.plantcare.data.repository.JournalRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AddEditJournalViewModel extends AndroidViewModel {

    private final JournalRepository journalRepo;
    private final JournalImageRepository imageRepo;

    private final MutableLiveData<String> content = new MutableLiveData<>();
    private final MutableLiveData<String> dateText = new MutableLiveData<>();
    private final MutableLiveData<List<String>> imageUrls = new MutableLiveData<>(new ArrayList<>());

    private Journal currentJournal = null;

    public AddEditJournalViewModel(@NonNull Application application) {
        super(application);
        journalRepo = new JournalRepository(application);
        imageRepo = new JournalImageRepository(application);
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

    public void addImageUri(String uri) {
        List<String> list = new ArrayList<>(imageUrls.getValue());
        if (list.size() < 6) {
            list.add(uri);
            imageUrls.setValue(list);
        }
    }

    public void loadJournal(int journalId) {
        journalRepo.getJournalById(journalId).observeForever(journal -> {
            if (journal != null) {
                currentJournal = journal;
                content.setValue(journal.getContent());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                dateText.setValue(journal.getDateCreated().format(formatter));

                // Lấy danh sách ảnh đồng bộ
                AppDatabase.databaseWriteExecutor.execute(() -> {
                    List<JournalImage> images = imageRepo.getImagesByJournalSync(journalId);
                    if (images != null && !images.isEmpty()) {
                        List<String> paths = new ArrayList<>();
                        for (JournalImage img : images) {
                            paths.add(img.getImageUrl());
                        }
                        imageUrls.postValue(paths);
                    }
                });
            }
        });
    }

    public void prepareNewJournalDate() {
        // Gọi khi tạo mới
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateText.setValue(LocalDateTime.now().format(formatter));
    }

    public void saveJournal(int plantId, String plantName) {
        String note = content.getValue() != null ? content.getValue() : "";
        List<String> imgs = imageUrls.getValue();

        if (currentJournal == null) {
            // Thêm mới
            Journal journal = new Journal();
            journal.setPlantId(plantId);
            journal.setPlantName(plantName);
            journal.setContent(note);
            journal.setDateCreated(LocalDateTime.now());

            long newId = journalRepo.insert(journal);

            if (imgs != null && !imgs.isEmpty()) {
                for (String img : imgs) {
                    JournalImage ji = new JournalImage();
                    ji.setJournalId((int) newId);
                    ji.setImageUrl(img);
                    imageRepo.insert(ji);
                }
            }

        } else {
            // Cập nhật
            currentJournal.setContent(note);
            journalRepo.update(currentJournal);

            if (imgs != null && !imgs.isEmpty()) {
                for (String img : imgs) {
                    JournalImage ji = new JournalImage();
                    ji.setJournalId(currentJournal.getJournalId());
                    ji.setImageUrl(img);
                    imageRepo.insert(ji);
                }
            }
        }
    }
}
