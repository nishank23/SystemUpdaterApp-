package com.lighko.updates;

import android.app.Application;
import android.content.Context;

import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import com.lighko.updates.services.DownloadWorker;
import com.lighko.updates.services.Workers.AutoInstallWorker;

import java.util.concurrent.TimeUnit;

public class MyApp extends Application implements Configuration.Provider {

    private static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        setupPeriodicUpdateCheck(this);
        scheduleAutoInstallWorker(this);
    }

    private void setupPeriodicUpdateCheck(Context context) {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(DownloadWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "UpdateCheck",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
        );

    }

    public static void scheduleAutoInstallWorker(Context context) {
        PeriodicWorkRequest autoInstallRequest = new PeriodicWorkRequest.Builder(AutoInstallWorker.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(context).enqueue(autoInstallRequest);

    }

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.INFO)
                .build();
    }
}
