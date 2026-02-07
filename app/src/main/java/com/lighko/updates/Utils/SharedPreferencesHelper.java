package com.lighko.updates.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.lighko.updates.Models.UpdateDetails;

import org.json.JSONException;
import org.json.JSONObject;

public class SharedPreferencesHelper {

    private static final String KEY_AUTO_DOWNLOAD = "auto_download";
    private static final String REBOOT_REQUIRED = "reboot_required";
    private static final String KEY_AUTO_UPDATE = "auto_update";
    private static final String KEY_UPDATE_DETAILS = "update_details";
    private static final String KEY_DOWNLOAD_COMPLETED = "download_completed";
    private static final String DOWNLOADED_BYTES = "downloaded_bytes";
    private static final String TOTAL_BYTES = "total_bytes";
    private static final String DOWNLOAD_STATUS = "download_status";
    private static final String KEY_UPDATE_ID = "update_id";
    private static final String KEY_VERSION_NUMBER = "version_number";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_UPDATE_FILE = "update_file";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_FILE_SIZE = "file_size";
    private static final String LAST_TIME_DEVICE_USED = "last_time_device_used";
    private static final String DeviceInfoUtil = "device_info_util";


    private SharedPreferences sharedPreferences;

    public SharedPreferencesHelper(Context context) {
        this.sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
    }

    public void setAutoDownloadEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AUTO_DOWNLOAD, enabled);
        editor.apply();
    }

    public boolean isAutoDownloadEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_DOWNLOAD, false);
    }

    public void setAutoUpdateEnabled(boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_AUTO_UPDATE, enabled);
        editor.apply();
    }

    public boolean isAutoUpdateEnabled() {
        return sharedPreferences.getBoolean(KEY_AUTO_UPDATE, false);
    }

    public void saveDeviceInfoDetails(String appVersion,String osVersion,String Imei,String serialNumber,String ModelNumber,String getCurrentDateTimeGmt,String getUpdateChannelName,String UpdateVersionNumber){



    }
    public void saveUpdateDetails(UpdateDetails updateDetails) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", updateDetails.getId());
            jsonObject.put("versionNumber", updateDetails.getVersionNumber());
            jsonObject.put("description", updateDetails.getDescription());
            jsonObject.put("updateFile", updateDetails.getUpdateFile());
            jsonObject.put("createdAt", updateDetails.getCreatedAt());
            jsonObject.put("fileSize", updateDetails.getFileSize());
            jsonObject.put("configFile",updateDetails.getConfigFile());
            jsonObject.put("channelName",updateDetails.getChannelName());

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_UPDATE_DETAILS, jsonObject.toString());
            editor.apply();
        } catch (JSONException e) {
            Log.e("SharedPreferencesHelper", "Failed to save update details", e);
        }
    }

    public UpdateDetails getUpdateDetails() {
        String detailsString = sharedPreferences.getString(KEY_UPDATE_DETAILS, null);
        if (detailsString != null) {
            try {
                JSONObject jsonObject = new JSONObject(detailsString);
                return new UpdateDetails(
                        jsonObject.getString("id"),
                        jsonObject.getString("versionNumber"),
                        jsonObject.getString("description"),
                        jsonObject.getString("updateFile"),
                        jsonObject.getString("createdAt"),
                        0, // Assuming __v is not used or set elsewhere as your constructor does not use it
                        jsonObject.getString("fileSize"),
                        jsonObject.getString("configFile"),
                        jsonObject.getString("channelName")
                        );
            } catch (JSONException e) {
                Log.e("SharedPreferencesHelper", "Failed to parse update details", e);
            }
        }
        return null;

    }

    public void setDownloadCompleted(boolean completed) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DOWNLOAD_COMPLETED, completed);
        editor.apply();
    }

    public boolean isDownloadCompleted() {
        return sharedPreferences.getBoolean(KEY_DOWNLOAD_COMPLETED, false);
    }
    public void setRebootRequired(boolean completed) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(REBOOT_REQUIRED, completed);
        editor.apply();
    }

    public boolean isRebootRequired() {
        return sharedPreferences.getBoolean(REBOOT_REQUIRED, false);
    }

    public void setDownloadedBytes(long bytes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DOWNLOADED_BYTES, bytes);
        editor.apply();
    }

    public long getDownloadedBytes() {
        return sharedPreferences.getLong(DOWNLOADED_BYTES, 0L);
    }

    public void setTotalBytes(long bytes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(TOTAL_BYTES, bytes);
        editor.apply();
    }

    public long getLastDeviceUseTime() {
        return sharedPreferences.getLong(LAST_TIME_DEVICE_USED, 0L);
    }

    public void setLastDeviceUseTime(long time) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TIME_DEVICE_USED, time);
        editor.apply();
    }

    public long getTotalBytes() {
        return sharedPreferences.getLong(TOTAL_BYTES, 0L);
    }

    public void setDownloadStatus(String status) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DOWNLOAD_STATUS, status);
        editor.apply();
    }

    public String getDownloadStatus() {
        return sharedPreferences.getString(DOWNLOAD_STATUS, "");
    }
}
