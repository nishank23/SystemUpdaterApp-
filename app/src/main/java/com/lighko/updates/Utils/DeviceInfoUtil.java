package com.lighko.updates.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.lighko.updates.Models.UpdateDetails;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DeviceInfoUtil {
    public static final String TAG  = "DeviceInfoUtil";
    private Context context;
    private SharedPreferencesHelper sharedPreferences;
    private UpdateDetails updateDetails;

    public DeviceInfoUtil(Context context, SharedPreferencesHelper sharedPreferences) {
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        updateDetails = this.sharedPreferences.getUpdateDetails();
    }
    public String getIMEI(){
        if (context.checkSelfPermission(android.Manifest.permission.READ_PRIVILEGED_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission READ_PRIVILEGED_PHONE_STATE not granted");
            return null;
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager!=null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                return telephonyManager.getImei();
            }
        }

        Log.e(TAG, "TelephonyManager is null");
        return null;
    }
    public String getSerialNumber() {
        return Build.getSerial();
    }

    public String getOSVersion() {
        try {
            Class<?> systemProperties = Class.forName("android.os.SystemProperties");
            Method get = systemProperties.getMethod("get", String.class);
            return (String) get.invoke(null, "ro.wonderos.version");
        } catch (Exception e) {
            e.printStackTrace();
            return Build.VERSION.RELEASE;
        }
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getAppVersion() {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    public String getUpdateChannelName() {
        return updateDetails.getChannelName();
    }

    public String getUpdateVersionNumber() {
        return updateDetails.getVersionNumber();
    }

    public String getCurrentDateTimeGMT() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }
}
