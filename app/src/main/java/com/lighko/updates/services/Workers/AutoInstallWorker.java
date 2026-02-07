package com.lighko.updates.services.Workers;

import static android.content.Context.BATTERY_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.lighko.updates.MyApp;
import com.lighko.updates.services.UpdateInstallationService;
import com.lighko.updates.Utils.SharedPreferencesHelper;

import java.util.Calendar;

public class AutoInstallWorker extends Worker {
    private SharedPreferencesHelper sharedPreferencesHelper;
    private Context context;
    private static final String TAG = "AutoInstallWorker";

    public AutoInstallWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context= context;
        this.sharedPreferencesHelper = new SharedPreferencesHelper(context);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Check if auto-update is enabled and the file is downloaded
        if (!sharedPreferencesHelper.isAutoUpdateEnabled() && !sharedPreferencesHelper.isDownloadCompleted()) {
            return Result.failure();
        }

        /*if (isWithinInstallWindow() && checkInstallConditions()) {
            // Trigger the installation process through the service
            if(triggerInstallation()){
                return Result.success();
            }else{
                return Result.failure();
            }

        }*/

        return Result.retry();
    }

    private boolean isWithinInstallWindow() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        return hour >= 6 && hour < 22;
    }

    private boolean checkInstallConditions() {
        PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = powerManager.isInteractive();

        // Check device usage and battery level
        long lastUsed = sharedPreferencesHelper.getLastDeviceUseTime();
        long currentTime = System.currentTimeMillis();
        boolean deviceIdle = (currentTime - lastUsed) > 30 * 60 * 1000; // 30 minutes

        BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY); // Implement this in SharedPreferencesHelper

        return /*!isScreenOn && deviceIdle &&*/ batLevel > 40;
    }

   /* private void triggerInstallation() {
        Intent serviceIntent = new Intent(MyApp.getInstance(), UpdateInstallationService.class);
        context.startForegroundService(serviceIntent);
    }
*/

    private boolean triggerInstallation(){
        try {
            Intent intent = new Intent(context, UpdateInstallationService.class);

            context.startForegroundService(intent);
            Log.i(TAG, "Installation service started successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error starting Installation service: " + e.getMessage(), e);
            return false;
        }
    }
}

