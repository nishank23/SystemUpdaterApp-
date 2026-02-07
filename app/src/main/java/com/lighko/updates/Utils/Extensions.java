package com.lighko.updates.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Extensions {
    /* Uncomment and modify according to actual use if needed
    public static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    */
    public static String convertMillisToTime(long millis) {
        try {
            long seconds = millis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            seconds = seconds % 60;
            minutes = minutes % 60;

            Log.d("RemainingTime", String.valueOf(millis));
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                if (networkCapabilities != null) {
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            }
        }
        return false;
    }

    public static double bytesToMB(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }

    public static String bytesToMBFormatted(long bytes, int decimals) {
        return String.format("%." + decimals + "f MB", bytesToMB(bytes));
    }

    public static void toast(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
