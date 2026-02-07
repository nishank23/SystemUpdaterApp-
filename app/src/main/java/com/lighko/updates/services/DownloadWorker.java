package com.lighko.updates.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.lighko.updates.Models.LatestUpdateResponse;
import com.lighko.updates.services.Repositories.UpdateRepository;
import com.lighko.updates.services.Workers.DownloadService;
import com.lighko.updates.ui.HomeActivity2;
import com.lighko.updates.R;
import com.lighko.updates.Utils.SharedPreferencesHelper;

import com.lighko.updates.Utils.Constants;

import java.util.concurrent.CountDownLatch;

public class DownloadWorker extends Worker {
    private static final String TAG = "DownloadWorker";
    private UpdateRepository updateRepository;
    private SharedPreferencesHelper sharedPreferencesHelper;

    public DownloadWorker(
            @NonNull Context context,
    @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.updateRepository = new UpdateRepository(context); // Assuming you have a context-based constructor
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        final CountDownLatch latch = new CountDownLatch(1);
        final Result[] result = new Result[1]; // Array to hold the result outside the callback
        if(sharedPreferencesHelper.isDownloadCompleted()){
            return Result.failure();
        }
        updateRepository.getLatestUpdate(new UpdateServiceImpl.Callback<LatestUpdateResponse>() {
            @Override
            public void onResponse(LatestUpdateResponse response) {
                if (response != null && handleUpdateResponse(response)) {
                    result[0] = Result.success();
                } else {
                    result[0] = Result.failure();
                }
                latch.countDown(); // Release the latch
            }

            @Override
            public void onFailure(Exception e) {
                result[0] = Result.failure();
                latch.countDown(); // Release the latch
            }

            @Override
            public void onMessage(String message) {

            }
        });

        try {
            latch.await(); // Wait for the callback to be called
        } catch (InterruptedException e) {
            return Result.failure();
        }
        return result[0];
    }

    private boolean handleUpdateResponse(LatestUpdateResponse response) {
        // Logic to check if new update is available
        boolean newUpdateAvailable = true; // Simulated logic
        if (newUpdateAvailable) {
            showUpdateNotification(response.getLatestUpdate().getVersionNumber());
            if (sharedPreferencesHelper.isAutoDownloadEnabled()) {
                String url = Constants.SERVER_URL + response.getLatestUpdate().getUpdateFile();
                String fileName = "WonderOS " + response.getLatestUpdate().getVersionNumber();
                return startDownloadService(getApplicationContext(), url, fileName);
            }
        }
        return true; // Return true if no auto-download is needed or update check fails
    }

    private void showUpdateNotification(String versionName) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "UpdateChannel";
        NotificationChannel channel = new NotificationChannel(
            channelId,
            "Update Notifications",
            NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Channel for update notifications");
        notificationManager.createNotificationChannel(channel);

        Intent intent = new Intent(getApplicationContext(), HomeActivity2.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
            .setSmallIcon(R.drawable.ic_update)
            .setContentTitle("New Update Available")
            .setContentText("WonderOS " + versionName + " is available. Tap to update.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify(1001, notificationBuilder.build());
    }

    private boolean startDownloadService(Context context, String url, String fileName) {
        try {
            Intent intent = new Intent(context, DownloadService.class);
            intent.putExtra("url", url);
            intent.putExtra("fileName", fileName);
            context.startForegroundService(intent);
            Log.i(TAG, "Download service started successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error starting download service: " + e.getMessage(), e);
            return false;
        }
    }
}
