package com.example.plantcare.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.dao.JournalDao;
import com.example.plantcare.data.dao.JournalImageDao;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.dao.TaskScopeDao;
import com.example.plantcare.data.entity.*;
import com.example.plantcare.utils.Converters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Plant.class, Task.class, TaskScope.class, Journal.class, JournalImage.class, History.class},
        version = 8, // Incremented version
        exportSchema = true
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract PlantDao plantDao();
    public abstract TaskDao taskDao();
    public abstract TaskScopeDao taskScopeDao();
    public abstract JournalDao journalDao();
    public abstract JournalImageDao journalImageDao();
    public abstract HistoryDao historyDao();

    private static volatile AppDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "plant_care_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
