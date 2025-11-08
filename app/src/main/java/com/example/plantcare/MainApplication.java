package com.example.plantcare;

import android.app.Application;

import com.example.plantcare.data.AppDatabase;

public class MainApplication extends Application {
    public static AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = AppDatabase.getDatabase(this);
    }
}
