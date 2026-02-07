package com.lighko.updates.services.BrodcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lighko.updates.Utils.SharedPreferencesHelper;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(context);
            sharedPreferencesHelper.setLastDeviceUseTime(System.currentTimeMillis());
        }
    }
}
