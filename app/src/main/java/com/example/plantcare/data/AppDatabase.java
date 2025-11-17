package com.example.plantcare.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
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

@Database(
        entities = {Plant.class, Task.class, TaskScope.class, Journal.class, JournalImage.class, History.class},
        version = 8, // Incremented version
        autoMigrations = {
                @AutoMigration(from = 5, to = 6)
        },
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
            String defaultDate = LocalDate.of(1970, 1, 1).format(DateTimeFormatter.ISO_LOCAL_DATE);
            database.execSQL("ALTER TABLE `Plant` ADD COLUMN `datePlanted` TEXT NOT NULL DEFAULT '" + defaultDate + "'");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE `Task_new` (`taskId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `frequency` INTEGER NOT NULL, `frequencyUnit` TEXT, `notifyStart` TEXT, `notifyEnd` TEXT, `isRepeat` INTEGER NOT NULL, `status` TEXT NOT NULL, `expiration` TEXT, `notifyTime` TEXT NOT NULL, `note` TEXT)");
            database.execSQL(
                    "INSERT INTO `Task_new` (`taskId`, `name`, `type`, `frequency`, `frequencyUnit`, `notifyStart`, `notifyEnd`, `isRepeat`, `status`, `expiration`, `notifyTime`, `note`) " +
                    "SELECT `taskId`, `name`, `type`, `frequency`, `frequencyUnit`, `notifyStart`, `notifyEnd`, `isRepeat`, `status`, `nextDue`, `notifyTime`, `note` FROM `Task`");
            database.execSQL("DROP TABLE `Task`");
            database.execSQL("ALTER TABLE `Task_new` RENAME TO `Task`");
            database.execSQL("CREATE TABLE IF NOT EXISTS `History` (`historyId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `taskName` TEXT NOT NULL, `taskType` TEXT NOT NULL, `status` TEXT NOT NULL, `content` TEXT NOT NULL, `notifyTime` TEXT NOT NULL, `dateCompleted` TEXT)");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Journal_new` (`journalId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `plantId` INTEGER, `plantName` TEXT NOT NULL, `dateCreated` TEXT NOT NULL, `content` TEXT, FOREIGN KEY(`plantId`) REFERENCES `Plant`(`plantId`) ON UPDATE NO ACTION ON DELETE SET NULL)");
            database.execSQL("INSERT INTO `Journal_new` (journalId, plantId, plantName, dateCreated, content) SELECT journalId, plantId, plantName, dateCreated, content FROM `Journal`");
            database.execSQL("DROP TABLE `Journal`");
            database.execSQL("ALTER TABLE `Journal_new` RENAME TO `Journal`");
            database.execSQL("CREATE INDEX `index_Journal_plantId` ON `Journal` (`plantId`)");
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Journal_new` (`journalId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `plantId` INTEGER NOT NULL, `plantName` TEXT NOT NULL, `dateCreated` TEXT NOT NULL, `content` TEXT)");
            database.execSQL("INSERT INTO `Journal_new` (journalId, plantId, plantName, dateCreated, content) SELECT journalId, plantId, plantName, dateCreated, content FROM `Journal`");
            database.execSQL("DROP TABLE `Journal`");
            database.execSQL("ALTER TABLE `Journal_new` RENAME TO `Journal`");
            database.execSQL("CREATE INDEX `index_Journal_plantId` ON `Journal` (`plantId`)");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "plant_care_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_6_7, MIGRATION_7_8)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
