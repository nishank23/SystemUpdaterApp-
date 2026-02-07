package com.lighko.updates.services.Repositories;

import android.content.Context;
import android.os.AsyncTask;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lighko.updates.Models.LatestUpdateResponse;
import com.lighko.updates.Utils.DownloadConfigFileTask;
import com.lighko.updates.Utils.Extensions;
import com.lighko.updates.services.UpdateServiceImpl;
import com.lighko.updates.Utils.Constants;
import com.lighko.updates.Utils.SharedPreferencesHelper;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.DownloadBlock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Extension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateRepository {
    private UpdateServiceImpl updateService;
    private Context context;
    private SharedPreferencesHelper sharedPreferences;
    public Fetch fetch;

    public UpdateRepository(Context context) {
        this.context = context;
        updateService = new UpdateServiceImpl();
        this.sharedPreferences = new SharedPreferencesHelper(context);

        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context).setDownloadConcurrentLimit(3).enableRetryOnNetworkGain(true).enableAutoStart(true).enableLogging(true)

                .build();
        this.fetch = Fetch.Impl.getInstance(fetchConfiguration);
    }

    public void getLatestUpdate(UpdateServiceImpl.Callback<LatestUpdateResponse> callback) {
        Map<String, String> parts = new HashMap<>();

        String buildNumber = "P61-r0-om8443-4g234571213206671_20240216_1245";
        String serialNumber = "0123456789ABCDEF";
        /*String buildNumber = Build.DISPLAY;
        String serialNumber = Build.getSerial();*/


        updateService.getLatestUpdate(buildNumber, serialNumber, new UpdateServiceImpl.Callback<LatestUpdateResponse>() {
            @Override
            public void onResponse(LatestUpdateResponse response) {
                if (response != null) {
                    sharedPreferences.saveUpdateDetails(response.getLatestUpdate());
                    callback.onResponse(response);
                }
            }

            @Override
            public void onFailure(Exception e) {

                Log.d("Api", e.getLocalizedMessage());
                callback.onFailure(e);
            }

            @Override
            public void onMessage(String message) {
                callback.onMessage(message);
            }
        });
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

    public void downloadFile(String url, File destFile, OnProgressListener onProgressListener) {
        Request request = new Request(url, destFile.getAbsolutePath());
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        sharedPreferences.setDownloadCompleted(false);

        fetch.enqueue(request, updatedRequest -> {
            // Handle post-download logic if necessary
        }, error -> {
            Log.d("ErrorFetch2", error.toString());
            // Log or handle errors
        });

        fetch.addListener(new FetchListener() {
            @Override
            public void onDeleted(@NonNull Download download) {

            }

            @Override
            public void onRemoved(@NonNull Download download) {

            }

            @Override
            public void onCancelled(@NonNull Download download) {

            }

            @Override
            public void onResumed(@NonNull Download download) {

            }

            @Override
            public void onPaused(@NonNull Download download) {

            }

            @Override
            public void onStarted(@NonNull Download download, @NonNull List<? extends DownloadBlock> list, int i) {

            }

            @Override
            public void onDownloadBlockUpdated(@NonNull Download download, @NonNull DownloadBlock downloadBlock, int i) {

            }

            @Override
            public void onError(@NonNull Download download, @NonNull Error error, @Nullable Throwable throwable) {
                Log.d("c", error.toString());

            }

            @Override
            public void onWaitingNetwork(@NonNull Download download) {

            }

            @Override
            public void onQueued(@NonNull Download download, boolean b) {

            }

            @Override
            public void onAdded(@NonNull Download download) {

            }

            @Override
            public void onProgress(Download download, long etaInMilliSeconds, long downloadedBytesPerSecond) {

                int progress = download.getProgress();
                long bytesDownloaded = download.getDownloaded();
                long etaProgress =
                        etaInMilliSeconds;
                Log.d("eta", String.valueOf(
                        etaProgress));


                Log.d("etaTime", Extensions.convertMillisToTime(
                        etaProgress));
                onProgressListener.onProgress(progress, bytesDownloaded, etaProgress);
            }

            @Override
            public void onCompleted(Download download) {
                sharedPreferences.setDownloadStatus("COMPLETED");

                sharedPreferences.setDownloadCompleted(true);
                onProgressListener.onProgress(100, download.getDownloaded(), download.getEtaInMilliSeconds());
                fetch.removeAll();
            }

            // Implement other necessary methods...
        });
    }

    public void downloadConfigFile() {
        new DownloadConfigFileTask(context, sharedPreferences).execute();
    }


    public interface OnProgressListener {
        void onProgress(int progress, long bytesDownloaded, long eta);
    }
}
