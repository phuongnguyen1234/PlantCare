package com.example.plantcare.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

//import com.example.plantcare.model.Plant;
//import com.example.plantcare.model.Task;
//import com.example.plantcare.util.Converters;

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

// 1. Thêm annotation @Database và khai báo các entities, version
@Database(entities = {Plant.class, Task.class, TaskScope.class, Journal.class, JournalImage.class, History.class}, version = 1, exportSchema = false)
// 6. Thêm annotation @TypeConverters nếu bạn cần chuyển đổi kiểu dữ liệu
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // 2. Khai báo các phương thức trừu tượng để lấy DAO
    public abstract PlantDao plantDao();
    public abstract TaskDao taskDao();
    public abstract TaskScopeDao taskScopeDao();
    public abstract JournalDao journalDao();
    public abstract JournalImageDao journalImageDao();
    public abstract HistoryDao historyDao();

    // 3. Tạo một instance duy nhất (Singleton) của AppDatabase
    private static volatile AppDatabase INSTANCE;

    // 4. Định nghĩa một ExecutorService để chạy các tác vụ database trên một luồng nền
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // 5. Tạo phương thức getDatabase để lấy instance của database
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "plant_care_database")
                            // .addCallback(sRoomDatabaseCallback) // Tùy chọn: Thêm callback nếu cần
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
