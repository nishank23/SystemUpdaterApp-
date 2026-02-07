package com.lighko.updates.services.BrodcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private NetworkChangeListener onNetworkChange;

    // Constructor with a NetworkChangeListener interface
    public NetworkChangeReceiver(NetworkChangeListener onNetworkChange) {
        this.onNetworkChange = onNetworkChange;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        onNetworkChange.onNetworkChange(isConnected);
    }

    // Define an interface to handle network changes
    public interface NetworkChangeListener {
        void onNetworkChange(boolean isConnected);
    }
}
