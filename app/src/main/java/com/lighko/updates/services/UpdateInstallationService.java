package com.lighko.updates.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.UpdateEngine;
import android.util.Log;
import android.view.View;

import com.lighko.updates.Models.UpdateDetails;
import com.lighko.updates.R;

import com.lighko.updates.UpdateConfig;
import com.lighko.updates.UpdateManager;

import androidx.core.app.NotificationCompat;

import com.lighko.updates.UpdaterState;
import com.lighko.updates.Utils.ConfigGenerator;
import com.lighko.updates.Utils.Constants;
import com.lighko.updates.Utils.Extensions;
import com.lighko.updates.Utils.FileUtils;
import com.lighko.updates.Utils.PropertyFile;
import com.lighko.updates.Utils.SharedPreferencesHelper;
import com.lighko.updates.ui.HomeActivity2;
import com.lighko.updates.util.UpdateConfigs;
import com.lighko.updates.util.UpdateEngineErrorCodes;
import com.lighko.updates.util.UpdateEngineStatuses;

import java.util.ArrayList;
import java.util.List;

public class UpdateInstallationService extends Service {
    private UpdateManager mUpdateManager = mUpdateManager = new UpdateManager(new UpdateEngine(), new Handler());
    ;
    private static final String TAG = "UpdateInstallationService";
    private static final String CHANNEL_ID = "UpdateChannel";
    private SharedPreferencesHelper sharedPreferencesHelper;
    private UpdateDetails updateDetails;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service onCreate called");
        setupUpdateManagerCallbacks();
        this.mUpdateManager.bind();
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        updateDetails = sharedPreferencesHelper.getUpdateDetails();
        createNotificationChannel();
        Log.d(TAG, "Service initialized successfully");
    }
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Update Service Channel",
                NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    private void setupUpdateManagerCallbacks() {
        mUpdateManager.setOnProgressUpdateCallback(this::onProgressUpdate);
        mUpdateManager.setOnEngineCompleteCallback(this::onEnginePayloadApplicationComplete);
        mUpdateManager.setOnStateChangeCallback(this::onUpdaterStateChange);
        mUpdateManager.setOnEngineStatusUpdateCallback(this::onEngineStatusUpdate);
    }

    private void onProgressUpdate(Double progress) {
        Notification notification = buildForegroundNotification("Installing update: " + (int) (progress * 100) + "%");
        startForeground(1, notification);
    }

    private void onEngineStatusUpdate(int status) {
        Log.i(TAG, "StatusEngineUpdate - status=" + UpdateEngineStatuses.getStatusText(status) + "/" + status);
        setUiEngineStatus(status);
    }
    private void onEnginePayloadApplicationComplete(int errorCode) {
        final String completionState = UpdateEngineErrorCodes.isUpdateSucceeded(errorCode) ? "SUCCESS" : "FAILURE";
        Log.i(TAG, "PayloadApplicationCompleted - errorCode=" + UpdateEngineErrorCodes.getCodeName(errorCode) + "/" + errorCode + " " + completionState);

//        runOnUiThread(() -> {
//            setUiEngineErrorCode(errorCode);
//        });
    }

    private void setUiEngineStatus(int status) {
        String statusText = UpdateEngineStatuses.getStatusText(status);

        if (status == 3) {

        } else if (status == 5) {


        } else if (status == 6) {


        }
    }

    private void onUpdaterStateChange(int state) {
        Log.i(TAG, "UpdaterStateChange state=" + UpdaterState.getStateText(state) + "/" + state);


        if (state == UpdaterState.IDLE) {

        } else if (state == UpdaterState.RUNNING) {
            // btnReboot.setVisibility(View.GONE);

        } else if (state == UpdaterState.PAUSED) {
        } else if (state == UpdaterState.ERROR) {
        } else if (state == UpdaterState.SLOT_SWITCH_REQUIRED) {
            // uiStateSlotSwitchRequired();
        } else if (state == UpdaterState.REBOOT_REQUIRED) {
        }

    }


    private Notification buildForegroundNotification(String contentText) {
        // Building a notification with contentText
        return new NotificationCompat.Builder(this, "update_channel")
                .setContentTitle("System Update")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_update)
                .build();
    }

    public String extractFileNameFromUrl(String url) {
        // Check for null or empty URL
        Log.d(TAG, "Extracting file name from URL: " + url);

        if (url == null || url.isEmpty()) {
            return "default.zip";
        }
        // Remove any query parameters that might exist in the URL
        int queryParamIndex = url.indexOf('?');
        if (queryParamIndex > -1) {
            url = url.substring(0, queryParamIndex);
        }
        // Find the last occurrence of '/' and extract the substring after it
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }
        // If there's no '/' or it's the last character, return a default filename
        return "default.zip";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service onStartCommand called");
        Notification notification = buildForegroundNotification("Starting update installation...");
        startForeground(1, notification);
        String configFile = extractFileNameFromUrl(Constants.SERVER_URL + sharedPreferencesHelper.getUpdateDetails().getUpdateFile());
        List<UpdateConfig> mConfigs = UpdateConfigs.getUpdateConfigs(this);
        UpdateConfig applyConfig = null;
        for (int i = 0; i < mConfigs.size(); i++) {
            if (extractFileNameFromUrl(mConfigs.get(i).getUrl()).equals(configFile)) {
                applyConfig = mConfigs.get(i);
            }
        }
        if (applyConfig == null) {
            Extensions.toast(this, "Ota not allowed because config mismatched on server");
        } else {
            applyUpdate(applyConfig);
        }
        return START_NOT_STICKY;
    }

    private void applyUpdate(UpdateConfig config) {
        try {
            Log.d(TAG, "Applying update with config: " + config.toString());

            mUpdateManager.applyUpdate(getApplicationContext(), config);
        } catch (Exception e) {
            Log.e(TAG, "Error occurred during update application: " + e.getMessage(), e);

            // Handle exception
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy called");

        mUpdateManager.setOnProgressUpdateCallback(null);
        mUpdateManager.setOnEngineCompleteCallback(null);


    }
}
