package com.lighko.updates.Utils;

import android.content.Context;

import com.lighko.updates.services.Repositories.UpdateRepository;

public class WorkerDependencyProvider {

    private static WorkerDependencyProvider instance;
    private final UpdateRepository updateRepository;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    // Private constructor to enforce singleton pattern
    private WorkerDependencyProvider(Context context) {
        // Assuming SharedPreferencesHelper and UpdateRepository are properly initialized with context
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
        this.updateRepository = new UpdateRepository(context);
    }

    // Public method to get singleton instance
    public static synchronized WorkerDependencyProvider getInstance(Context context) {
        if (instance == null) {
            instance = new WorkerDependencyProvider(context);
        }
        return instance;
    }

    public UpdateRepository getUpdateRepository() {
        return updateRepository;
    }

    public SharedPreferencesHelper getSharedPreferencesHelper() {
        return sharedPreferencesHelper;
    }
}
