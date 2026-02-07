package com.lighko.updates.services.Workers;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.lighko.updates.Utils.SharedPreferencesHelper;
import com.lighko.updates.services.Repositories.UpdateRepository;
import com.lighko.updates.services.BrodcastReceivers.NetworkChangeReceiver;
import com.lighko.updates.ui.HomeActivity2;
import com.lighko.updates.R;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadService extends Service {
    private static final String CHANNEL_ID = "DownloadChannel";
    private static final int NOTIFICATION_ID = 1;
    public static final String BROADCAST_ACTION = "com.lighko.updates.services.Workers.DownloadService.PROGRESS_UPDATE";

    private UpdateRepository updateRepository;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private NetworkChangeReceiver networkChangeReceiver;
    private String currentUrl;
    private File currentFile;
    private int lastProgress =-1;
    private String notificationTitle;

    private boolean isDownloaded = false;


    public static final MutableLiveData<Integer> downloadProgress = new MutableLiveData<>();
    public static final MutableLiveData<Long> bytesDownloadProgress = new MutableLiveData<>();
    public static final MutableLiveData<Long> etaProgress = new MutableLiveData<>();

    @Override
    public void onCreate() {
        super.onCreate();
        updateRepository = new UpdateRepository(this);  // Make sure the constructor matches your setup
        sharedPreferencesHelper = new SharedPreferencesHelper(this);
        isDownloaded=sharedPreferencesHelper.isDownloadCompleted();
        createNotificationChannel();
        registerNetworkReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String url = intent.getStringExtra("url");
        String fileName = intent.getStringExtra("fileName");
        notificationTitle = fileName;
        String otaFile = extractFileNameFromUrl(url);
        File destFile = new File(this.getFilesDir().toString(), otaFile);
        currentUrl = url;
        currentFile = destFile;
        if (url != null && fileName != null) {
            startForeground(NOTIFICATION_ID, createNotification(fileName, 0, false, false));

            if (!isDownloaded) {
                startDownload(url, destFile);
            }
           // startDownload(url, destFile);
        }
        return START_NOT_STICKY;
    }

    public String extractFileNameFromUrl(String url) {
        try {
            URL urlObject = new URL(url);
            // Extract the file name from the URL path
            String fileName = urlObject.getPath().substring(urlObject.getPath().lastIndexOf('/') + 1);
            return fileName;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Return a default file name or handle the error as needed
            return "default.zip";
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Download Service Channel",
                    NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    private Notification createNotification(String contentText, int progress, boolean downloadComplete, boolean waitingForInternet) {
        Intent intent = new Intent(this, HomeActivity2.class);

        intent.putExtra("downloadComplete",downloadComplete);
        intent.putExtra("waitingNetwork",waitingForInternet);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String actionText ="";
        if (downloadComplete) {
            actionText ="Click to Install";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(waitingForInternet ? "No Internet" : notificationTitle)
                .setContentText(downloadComplete ? "Download complete." : contentText)
                .setSmallIcon(R.drawable.ic_download)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(pendingIntent)
                .setProgress(100, progress, false)
                .setAutoCancel(downloadComplete);



        if(downloadComplete){
            builder.addAction(R.drawable.ic_update,actionText,pendingIntent);
        }

        return builder.build();
    }

    private void startDownload(String url, File destFile) {
        updateRepository.downloadFile(url, destFile, (progress, bytes,eta) -> {
            downloadProgress.postValue(progress);
            bytesDownloadProgress.postValue(bytes);
            etaProgress.postValue(eta);
            sendProgressBroadcast(progress, bytes,eta);

            lastProgress = progress;

            Notification notification = createNotification("Downloading... "+progress+"%",progress,false,false);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.notify(NOTIFICATION_ID,notification);
            manager.cancel(1001);

            if(progress==100){
                Toast.makeText(this, "Downloading the config file ", Toast.LENGTH_SHORT).show();
                updateRepository.downloadConfigFile();
                sharedPreferencesHelper.setDownloadCompleted(true);
                Notification completeNotification = createNotification("Download completed",100,true,false);
                manager.notify(NOTIFICATION_ID,completeNotification);
                stopForeground(false);
            }

        });
    }

    private void registerNetworkReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver(isConnected -> {
            if (isConnected && !isDownloaded) {
                Log.d("DownloadStarted","Download Started");
                startDownload(currentUrl, currentFile);
            }
            if (!isConnected&&lastProgress!=100) {
                Notification notification = createNotification("Waiting For Internet...", 0, false, true);
                getSystemService(NotificationManager.class).notify(NOTIFICATION_ID, notification);
            }
        });
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, filter);
    }

    private void sendProgressBroadcast(int progress, long bytesDownloaded,long eta) {
        Intent intent = new Intent(BROADCAST_ACTION);
        intent.putExtra("progress", progress);
        intent.putExtra("bytes", bytesDownloaded);
        intent.putExtra("eta", eta);
        sendBroadcast(intent);
    }
}
