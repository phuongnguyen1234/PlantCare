package com.example.plantcare.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.plantcare.data.dao.HistoryDao;
import com.example.plantcare.data.dao.JournalDao;
import com.example.plantcare.data.dao.JournalImageDao;
import com.example.plantcare.data.dao.PlantDao;
import com.example.plantcare.data.dao.TaskDao;
import com.example.plantcare.data.dao.TaskScopeDao;
import com.example.plantcare.data.entity.*;
import com.example.plantcare.utils.Converters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Database version is now 3
@Database(entities = {Plant.class, Task.class, TaskScope.class, Journal.class, JournalImage.class, History.class}, version = 3, exportSchema = false)
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

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE `Plant_new` (`plantId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `imageUrl` TEXT, `temperatureRange` TEXT, `humidityRange` TEXT, `waterFrequency` INTEGER NOT NULL, `fertilizerFrequency` INTEGER NOT NULL, `lightFrequency` INTEGER NOT NULL, `waterUnit` TEXT, `fertilizerUnit` TEXT, `lightUnit` TEXT, `note` TEXT)");
            database.execSQL(
                    "INSERT INTO `Plant_new` (`plantId`, `name`, `imageUrl`, `temperatureRange`, `humidityRange`, `waterFrequency`, `fertilizerFrequency`, `lightFrequency`, `waterUnit`, `fertilizerUnit`, `lightUnit`, `note`) " +
                    "SELECT `plantId`, `name`, `imageUrl`, `temperatureRange`, `humidityRange`, `waterFrequency`, COALESCE(`fertilizerFrequency`, 0), COALESCE(`lightFrequency`, 0), `waterUnit`, `fertilizerUnit`, `lightUnit`, `note` FROM `Plant`");
            database.execSQL("DROP TABLE `Plant`");
            database.execSQL("ALTER TABLE `Plant_new` RENAME TO `Plant`");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add the new datePlanted column as TEXT, which matches the (now correct) TypeConverter
            // Provide a default value in the correct ISO_LOCAL_DATE format for existing rows.
            String defaultDate = LocalDate.of(1970, 1, 1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            database.execSQL("ALTER TABLE `Plant` ADD COLUMN `datePlanted` TEXT NOT NULL DEFAULT '" + defaultDate + "'");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "plant_care_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
