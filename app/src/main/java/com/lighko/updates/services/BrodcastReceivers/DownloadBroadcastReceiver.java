package com.lighko.updates.services.BrodcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    private MutableLiveData<Integer> progressLiveData;
    private MutableLiveData<Long> bytesDownloadedLiveData;
    private MutableLiveData<Long> etaProgress;

    public DownloadBroadcastReceiver(MutableLiveData<Integer> progressLiveData, MutableLiveData<Long> bytesDownloadedLiveData,MutableLiveData<Long> eta) {
        this.progressLiveData = progressLiveData;
        this.bytesDownloadedLiveData = bytesDownloadedLiveData;
        this.etaProgress =eta;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            int progress = intent.getIntExtra("progress", 0);
            long bytes = intent.getLongExtra("bytes", 0L);
            long eta = intent.getLongExtra("eta", 0L);
            progressLiveData.postValue(progress);
            bytesDownloadedLiveData.postValue(bytes);
            etaProgress.postValue(eta);
        }
    }
}
