package com.example.plantcare.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "JournalImage",
        foreignKeys = @ForeignKey(
                entity = Journal.class,
                parentColumns = "journalId",
                childColumns = "journalId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = {"journalId"})
)
public class JournalImage {
    @PrimaryKey(autoGenerate = true)
    private int imageId;

    private int journalId;

    @NonNull
    private String imageUrl = "android.resource://com.example.plantcare/drawable/default_plant";

    private int position = 0;

    public JournalImage() {
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getJournalId() {
        return journalId;
    }

    public void setJournalId(int journalId) {
        this.journalId = journalId;
    }

    @NonNull
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(@NonNull String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
