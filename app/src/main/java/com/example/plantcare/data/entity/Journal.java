package com.example.plantcare.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

@Entity(tableName = "Journal")
public class Journal {
    @PrimaryKey(autoGenerate = true)
    private int journalId;


    private int plantId;

    @NonNull
    private String plantName = "Cây của tôi";

    @NonNull
    private LocalDate dateCreated = LocalDate.now();
    private String content;

    public Journal() {
    }

    public int getJournalId() {
        return journalId;
    }

    public void setJournalId(int journalId) {
        this.journalId = journalId;
    }

    public int getPlantId() {
        return plantId;
    }

    public void setPlantId(int plantId) {
        this.plantId = plantId;
    }

    @NonNull
    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(@NonNull String plantName) {
        this.plantName = plantName;
    }

    @NonNull
    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(@NonNull LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
