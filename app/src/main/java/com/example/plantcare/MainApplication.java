package com.example.plantcare;

import android.app.Application;

/**
 * The Application class. The database instance is now managed by a singleton pattern
 * in AppDatabase.getDatabase() and accessed via dependency injection in repositories.
 * This class can be used for other application-level initializations if needed in the future.
 */
public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // The static database instance is no longer needed here.
        // Repositories now get the database instance via AppDatabase.getDatabase(context).
    }
}
