package com.lighko.updates.viewModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lighko.updates.Models.LatestUpdateResponse;
import com.lighko.updates.Models.UpdateDetails;
import com.lighko.updates.Utils.Extensions;
import com.lighko.updates.Utils.SharedPreferencesHelper;
import com.lighko.updates.services.Repositories.UpdateRepository;
import com.lighko.updates.services.UpdateServiceImpl;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2core.DownloadBlock;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateViewModel extends ViewModel {
    private UpdateRepository updateRepository;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private MutableLiveData<UpdateDetails> _updateDetails = new MutableLiveData<>();
    public LiveData<UpdateDetails> updateDetails = _updateDetails;

    private MutableLiveData<Integer> _downloadProgress = new MutableLiveData<>();
    public LiveData<Integer> downloadProgress = _downloadProgress;

    private MutableLiveData<Long> _downloadedBytes = new MutableLiveData<>();
    public LiveData<Long> downloadedBytes = _downloadedBytes;


    private MutableLiveData<Long> _etaProgress = new MutableLiveData<>();
    public LiveData<Long> etaProgress = _etaProgress;


    private MutableLiveData<String> downloadStatus = new MutableLiveData<>();
    public MutableLiveData<String> updateStatus = new MutableLiveData<>();

    public UpdateViewModel(UpdateRepository updateRepository, SharedPreferencesHelper sharedPreferencesHelper) {
        this.updateRepository = updateRepository;
        this.sharedPreferencesHelper = sharedPreferencesHelper;

        UpdateDetails savedUpdateDetails = sharedPreferencesHelper.getUpdateDetails();
        if (savedUpdateDetails != null) {
            _updateDetails.postValue(savedUpdateDetails);
        }

        updateRepository.fetch.addListener(new FetchListener() {
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

            }

            @Override
            public void onCompleted(@NonNull Download download) {

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
                Log.d("ViewmodelStatus", download.getStatus().toString());
                _downloadProgress.postValue(download.getProgress());
                _downloadedBytes.postValue(download.getDownloaded());
                _etaProgress.postValue(download.getEtaInMilliSeconds());
                downloadStatus.postValue(download.getStatus().toString());
                sharedPreferencesHelper.setDownloadStatus(download.getStatus().toString());
            }

            // Implement other necessary override methods...
        });
    }

    public void fetchLatestUpdate() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Log.d("ApiCalled", "this is the api");
        executor.submit(() -> {
            try {
                updateRepository.getLatestUpdate(new UpdateServiceImpl.Callback<LatestUpdateResponse>() {
                    @Override
                    public void onResponse(LatestUpdateResponse response) {
                        _updateDetails.postValue(response.getLatestUpdate());
                        sharedPreferencesHelper.saveUpdateDetails(response.getLatestUpdate());
                        Log.d("Updated_Values", response.getLatestUpdate().toString());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("Error", "Error Occurred: " + e.toString() + e.getMessage().toString());
                    }

                    @Override
                    public void onMessage(String message) {
                        updateStatus.postValue(message);
                    }
                });
            } catch (Exception e) {
                Log.e("Error", "Error Occurred: " + e.getMessage());
            }
        });
    }
}
