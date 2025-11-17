package com.example.plantcare.data.model;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.plantcare.data.entity.Journal;
import com.example.plantcare.data.entity.JournalImage;

import java.util.List;

public class JournalWithImages {

    @Embedded
    public Journal journal;

    @Relation(
            parentColumn = "journalId",
            entityColumn = "journalId"
    )
    public List<JournalImage> images;
}
