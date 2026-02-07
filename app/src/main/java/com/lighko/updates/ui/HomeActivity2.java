package com.lighko.updates.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.UpdateEngine;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.lighko.updates.Adapters.SimplePopup;
import com.lighko.updates.Adapters.SimplePopupValue;
import com.lighko.updates.Models.UpdateDetails;
import com.lighko.updates.UpdaterState;
import com.lighko.updates.Utils.DeviceInfoUtil;
import com.lighko.updates.Utils.Extensions;
import com.lighko.updates.Utils.FileUtils;
import com.lighko.updates.services.Workers.DownloadService;
import com.lighko.updates.viewModels.UpdateViewModel;

import com.lighko.updates.UpdateConfig;
import com.lighko.updates.UpdateManager;
import com.lighko.updates.Utils.Constants;
import com.lighko.updates.Utils.SharedPreferencesHelper;
import com.lighko.updates.services.BrodcastReceivers.DownloadBroadcastReceiver;
import com.lighko.updates.services.BrodcastReceivers.NetworkChangeReceiver;
import com.lighko.updates.services.Repositories.UpdateRepository;
import com.lighko.updates.util.UpdateConfigs;
import com.lighko.updates.util.UpdateEngineErrorCodes;
import com.lighko.updates.util.UpdateEngineStatuses;
import com.lighko.updates.viewModels.UpdateViewModelFactory;
import com.lighko.updates.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeActivity2 extends AppCompatActivity {
    private SimplePopup simplePopup = null;
    private UpdateViewModel updateViewModel;
    private SharedPreferencesHelper sharedPreferences;
    private DownloadBroadcastReceiver downloadBroadcastReceiver;
    private NetworkChangeReceiver networkChangeReceiver;
    private ProgressBar progressCircular;
    private ScrollView sv1;
    private RelativeLayout rlBottom;
    private ConstraintLayout pbDownload, download, updateTips;
    private TextView mtvUpdateMsg, mtvUpdateMsg2, mtvDownloading, mtvTtlDown, mtvVerSize, mtvVerTitle, mtvDesc1, mtvDownPercent, mtvDown;
    private Button btnDownload, btnCheckUpdates, btnInstall, btnReboot;
    private Button btnMore;
    private ProgressBar piDownload;
    private static final String TAG = "HomeActivity";
    private UpdateDetails updateDetails;
    private final UpdateManager mUpdateManager = new UpdateManager(new UpdateEngine(), new Handler());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bgColor));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_home);
        progressCircular = findViewById(R.id.progress_circular);
        sv1 = findViewById(R.id.sv_1);
        rlBottom = findViewById(R.id.rl_bottom);
        pbDownload = findViewById(R.id.pb_download);
        mtvUpdateMsg = findViewById(R.id.mtv_update_msg);
        mtvUpdateMsg2 = findViewById(R.id.mtv_update_msg2);
        btnDownload = findViewById(R.id.btn_download);
        btnCheckUpdates = findViewById(R.id.btn_check_updates);
        btnMore = findViewById(R.id.btn_more);
        btnInstall = findViewById(R.id.btn_install);
        //mtvDownloading = findViewById(R.id.mtv_downloading);
        btnReboot = findViewById(R.id.btn_reboot);
        mtvTtlDown = findViewById(R.id.mtv_ttl_down);
        mtvVerSize = findViewById(R.id.mtv_ver_size);
        mtvVerTitle = findViewById(R.id.mtv_ver_title);
        mtvDesc1 = findViewById(R.id.mtv_desc_1);
        download = findViewById(R.id.download);
        updateTips = findViewById(R.id.update_tips);
        piDownload = findViewById(R.id.pi_download);
        mtvDownPercent = findViewById(R.id.mtv_down_percent);
        mtvDown = findViewById(R.id.mtv_down);
        initialize();
        onListeners();
        this.mUpdateManager.setOnStateChangeCallback(this::onUpdaterStateChange);
        this.mUpdateManager.setOnEngineStatusUpdateCallback(this::onEngineStatusUpdate);
        this.mUpdateManager.setOnEngineCompleteCallback(this::onEnginePayloadApplicationComplete);
        this.mUpdateManager.setOnProgressUpdateCallback(this::onProgressUpdate);

        getDeviceDetails();


        /*simulateProgressUpdates();*/
    }

    public void getDeviceDetails() {
        // Log.i("DeviceDetails", "SERIAL: " + Build.getSerial());
        Log.i("DeviceDetails", "MODEL: " + Build.MODEL);
        Log.i("DeviceDetails", "ID: " + Build.ID);
        Log.i("DeviceDetails", "Manufacture: " + Build.MANUFACTURER);
        Log.i("DeviceDetails", "brand: " + Build.BRAND);
        Log.i("DeviceDetails", "type: " + Build.TYPE);
        Log.i("DeviceDetails", "user: " + Build.USER);
        Log.i("DeviceDetails", "BASE: " + Build.VERSION_CODES.BASE);
        Log.i("DeviceDetails", "INCREMENTAL " + Build.VERSION.INCREMENTAL);
        Log.i("DeviceDetails", "SDK  " + Build.VERSION.SDK);
        Log.i("DeviceDetails", "BOARD: " + Build.BOARD);
        Log.i("DeviceDetails", "BRAND " + Build.BRAND);
        Log.i("DeviceDetails", "HOST " + Build.HOST);
        Log.i("DeviceDetails", "FINGERPRINT: " + Build.FINGERPRINT);
        Log.i("DeviceDetails", "Version Code: " + Build.VERSION.RELEASE);
        Log.i("DeviceDetails", "DISPLAY: " + Build.DISPLAY);
    }

    /*  07-10 19:15:01.018  2222  2222 I DeviceDetails: SERIAL: 0123456789ABCDEF
  07-10 19:15:01.019  2222  2222 I DeviceDetails: MODEL: k61v1_32_bsp_1g
  07-10 19:15:01.019  2222  2222 I DeviceDetails: ID: RP1A.200720.011
  07-10 19:15:01.019  2222  2222 I DeviceDetails: Manufacture: Along
  07-10 19:15:01.019  2222  2222 I DeviceDetails: brand: alps
  07-10 19:15:01.019  2222  2222 I DeviceDetails: type: userdebug
  07-10 19:15:01.019  2222  2222 I DeviceDetails: user: kevilpatel49
  07-10 19:15:01.019  2222  2222 I DeviceDetails: BASE: 1
  07-10 19:15:01.019  2222  2222 I DeviceDetails: INCREMENTAL mp1k61v164bspP4
  07-10 19:15:01.019  2222  2222 I DeviceDetails: SDK  30
  07-10 19:15:01.019  2222  2222 I DeviceDetails: BOARD: k61v1_32_bsp_1g
  07-10 19:15:01.019  2222  2222 I DeviceDetails: BRAND alps
  07-10 19:15:01.020  2222  2222 I DeviceDetails: HOST instance-20240303-171335
  07-10 19:15:01.020  2222  2222 I DeviceDetails: FINGERPRINT: alps/full_k61v1_32_bsp_1g/k61v1_32_bsp_1g:11/RP1A.200720.011/mp1k61v164bspP4:userdebug/release-keys
  07-10 19:15:01.020  2222  2222 I DeviceDetails: Version Code: 11
  07-10 19:15:01.020  2222  2222 I DeviceDetails: Build Display: P61-r0-om8443-4g234571213206671_20240710_1234

      */
    private void onEngineStatusUpdate(int status) {
        Log.i(TAG, "StatusEngineUpdate - status=" + UpdateEngineStatuses.getStatusText(status) + "/" + status);


        setUiEngineStatus(status);

    }

    private void onEnginePayloadApplicationComplete(int errorCode) {
        final String completionState = UpdateEngineErrorCodes.isUpdateSucceeded(errorCode) ? "SUCCESS" : "FAILURE";
        Log.i(TAG, "PayloadApplicationCompleted - errorCode=" + UpdateEngineErrorCodes.getCodeName(errorCode) + "/" + errorCode + " " + completionState);
    }

    private void setUiEngineStatus(int status) {
        String statusText = UpdateEngineStatuses.getStatusText(status);

        if (status == 3) {
            Log.e(TAG, "onInstalling");
            runOnUiThread(this::onInstalling);
            updateSharedPreferencesRebootRequired(false);
        } else if (status == 4) {
            Log.e(TAG, "onVerifying");
            runOnUiThread(this::onVerifying);
        } else if (status == 5) {
            Log.e(TAG, "onFinalizing");

            runOnUiThread(this::onFinalizing);
        } else if (status == 6) {
            Log.e(TAG, "onReboot");
            sharedPreferences.setRebootRequired(true);
            sharedPreferences.setDownloadCompleted(false);
            runOnUiThread(this::onSuccess);
            executorService.execute(() -> {
                FileUtils.clearFilesDir(this);
            });
        }
    }

    private void onUpdaterStateChange(int state) {
        Log.i(TAG, "UpdaterStateChange state=" + UpdaterState.getStateText(state) + "/" + state);

        runOnUiThread(() -> {
            if (state == UpdaterState.IDLE) {
                Log.e(TAG, "onIdleonUpdaterStateChange");

                if (isDownloadCompleted()) {
                    btnInstall.setVisibility(View.VISIBLE);
                }
                btnReboot.setVisibility(View.INVISIBLE);
                updateSharedPreferencesRebootRequired(false);
            } else if (state == UpdaterState.RUNNING) {
                Log.e(TAG, "onRunningonUpdaterStateChange");

                btnInstall.setVisibility(View.GONE);

            } else if (state == UpdaterState.PAUSED) {
                mtvUpdateMsg.setText("PAUSED");
            } else if (state == UpdaterState.ERROR) {
                onError();
            } else if (state == UpdaterState.SLOT_SWITCH_REQUIRED) {
                // uiStateSlotSwitchRequired();
            } else if (state == UpdaterState.REBOOT_REQUIRED) {
                Log.e(TAG, "onRebootonUpdaterStateChange");
                onSuccess();
                //onSuccess();
            }
        });
    }

    // UI Update methods remain the same
    private void onInstalling() {
        mtvDown.setVisibility(View.GONE);
        mtvTtlDown.setVisibility(View.GONE);
        mtvVerSize.setVisibility(View.GONE);
        pbDownload.setVisibility(View.VISIBLE);
        mtvUpdateMsg.setText("Installing...");
    }

    private void onVerifying() {
        mtvUpdateMsg.setText("Verifying...");
    }


    private void onFinalizing() {
        mtvUpdateMsg.setText("Finalizing...");
    }

    private void onSuccess() {
        if (btnReboot != null) {
            btnReboot.setVisibility(View.VISIBLE);
        } else {
            Log.e(TAG, "btnReboot is null");
        }

        if (mtvUpdateMsg2 != null) {
            mtvUpdateMsg2.setVisibility(View.VISIBLE);
            mtvUpdateMsg2.setText("Restart the device to see the wonder");
        } else {
            Log.e(TAG, "mtvUpdateMsg2 is null");
        }

        if (mtvUpdateMsg != null) {
            mtvUpdateMsg.setText("Updated successful!");
        } else {
            Log.e(TAG, "mtvUpdateMsg is null");
        }

        if (btnDownload != null) {
            btnDownload.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "btnDownload is null");
        }

        if (pbDownload != null) {
            pbDownload.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "pbDownload is null");
        }
    }


    private void onError() {
        btnInstall.setVisibility(View.GONE);
        mtvUpdateMsg.setText("Update Failed");
        mtvUpdateMsg2.setVisibility(View.VISIBLE);
        mtvUpdateMsg2.setText("Please try again\nIf this happens for the second time, please contact Wonder for help.");
        sharedPreferences.setDownloadCompleted(false);
        btnDownload.setVisibility(View.VISIBLE);
        btnDownload.setText("Download again");

        new Thread(() -> {
            FileUtils.clearFilesDir(this); // Move file operations to background thread
        }).start();
    }

    private void setUiEngineErrorCode(int errorCode) {
        String errorText = UpdateEngineErrorCodes.getCodeName(errorCode);
        Log.d(TAG, errorText + "/" + errorCode);

    }

    // Method to update SharedPreferences in a background thread
    private void updateSharedPreferencesRebootRequired(boolean isRequired) {
        new Thread(() -> sharedPreferences.setRebootRequired(isRequired)).start();
    }


    private void initialize() {
        sharedPreferences = new SharedPreferencesHelper(this);
        updateDetails = sharedPreferences.getUpdateDetails();
        UpdateViewModelFactory factory = new UpdateViewModelFactory(new UpdateRepository(this), new SharedPreferencesHelper(this));
        updateViewModel = new ViewModelProvider(this, factory).get(UpdateViewModel.class);
        MutableLiveData<Integer> downloadProgress = (MutableLiveData<Integer>) updateViewModel.downloadProgress;
        MutableLiveData<Long> downloadedBytes = (MutableLiveData<Long>) updateViewModel.downloadedBytes;
        MutableLiveData<Long> etaProgress = (MutableLiveData<Long>) updateViewModel.etaProgress;

        // Create an instance of the DownloadBroadcastReceiver
        downloadBroadcastReceiver = new DownloadBroadcastReceiver(downloadProgress, downloadedBytes, etaProgress);
        Integer progress = updateViewModel.downloadProgress.getValue();
        if (!Extensions.isInternetAvailable(this)) {
            progressCircular.setVisibility(View.GONE);
            onInternetLost();
            sv1.setVisibility(View.VISIBLE);
            rlBottom.setVisibility(View.VISIBLE);
        } else if (!isDownloadCompleted() && !isRebootRequired() && progress == null && Extensions.isInternetAvailable(this)) {

            Log.e(TAG, "OVERHERE 1");
            updateViewModel.fetchLatestUpdate();
        } else if (!sharedPreferences.isDownloadCompleted() && updateDetails != null && progress == null && Extensions.isInternetAvailable(this)) {
            Log.e(TAG, "OVERHERE 2");

            progressCircular.setVisibility(View.GONE);
            sv1.setVisibility(View.VISIBLE);
            rlBottom.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getDownloadStatus().equals("DOWNLOADING") && !Extensions.isInternetAvailable(this)) {
            Log.e(TAG, "OVERHERE 3");

            onDownload();
        }
        if (isDownloadCompleted() && !isRebootRequired()) {
            Log.e(TAG, "OVERHERE 4");

            sharedPrefDownloaded();
        }


    }

    /*private void onTesting() {
        pbDownload.setVisibility(View.VISIBLE);
        piDownload.setVisibility(View.VISIBLE);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int up_progress = (int) (100 * test_progress);
                piDownload.setProgress(up_progress);
                String strUpProgress = String.valueOf(up_progress) + "%";
                mtvDownPercent.setText(strUpProgress);
                test_progress = test_progress + .01;

                handler.postDelayed(this, 500);
            }
        }, 500);
       // onSuccess();

    }
*/

    private void onInternetLost() {
        mtvUpdateMsg.setText("Network unavailable");
        mtvUpdateMsg2.setVisibility(View.VISIBLE);
        mtvUpdateMsg2.setText("Please check again later");
    }

    @Override
    protected void onDestroy() {
        this.mUpdateManager.setOnEngineStatusUpdateCallback(null);
        this.mUpdateManager.setOnProgressUpdateCallback(null);
        this.mUpdateManager.setOnEngineCompleteCallback(null);
        super.onDestroy();
        executorService.shutdown();

    }

    @Override
    protected void onPause() {
        this.mUpdateManager.unbind();
        super.onPause();
        unregisterReceiver(downloadBroadcastReceiver);
        unregisterReceiver(networkChangeReceiver);
    }




    private void simulateProgressUpdates() {
        // Simulate a series of progress updates
        pbDownload.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                for (int i = 0; i <= 100; i += 1) {
                    // Simulate progress update
                    double progress = i / 100.0;
                    runOnUiThread(() -> onProgressUpdate(progress));

                    // Sleep for a while to simulate time taken for progress
                    Thread.sleep(500); // 500 ms delay
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void onProgressUpdate(double progress) {
        runOnUiThread(() -> {
            int up_progress = (int) (100 * progress);
            piDownload.setProgress(up_progress);
            String strUpProgress = up_progress + "%";
            mtvDownPercent.setText(strUpProgress);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        this.mUpdateManager.bind();
        registerReceiver(downloadBroadcastReceiver, new IntentFilter(DownloadService.BROADCAST_ACTION), RECEIVER_VISIBLE_TO_INSTANT_APPS);
        registerNetworkReceiver();


    }

    private void applyUpdate(UpdateConfig config) {
        try {

            mUpdateManager.applyUpdate(this, config);
        } catch (UpdaterState.InvalidTransitionException e) {
            onError();
            Log.e(TAG, "Failed to apply update " + config.getName(), e);
        }
    }

    private void onReboot() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        pm.reboot(null);
    }

    private void onListeners() {
        btnReboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReboot();
            }
        });
        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomPopup(v);
            }
        });
        btnCheckUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Extensions.isInternetAvailable(HomeActivity2.this)) {
                    Extensions.toast(HomeActivity2.this, "No Internet Connection");
                    return;
                }
                updateViewModel.fetchLatestUpdate();
            }
        });

        btnInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInstall();
            }
        });

        updateViewModel.updateStatus.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                progressCircular.setVisibility(View.GONE);
                sv1.setVisibility(View.VISIBLE);
                rlBottom.setVisibility(View.VISIBLE);
                Extensions.toast(HomeActivity2.this, s);
            }
        });
        updateViewModel.updateDetails.observe(this, new Observer<UpdateDetails>() {
            @Override
            public void onChanged(UpdateDetails updateDetails) {

                if (!sharedPreferences.isDownloadCompleted() && updateDetails != null && Extensions.isInternetAvailable(HomeActivity2.this) && !isFileCurrentlyDownloading()) {

                    Log.d(TAG, "VIEWMODEL UPDATE CALLED");
                    progressCircular.setVisibility(View.GONE);
                    sv1.setVisibility(View.VISIBLE);
                    rlBottom.setVisibility(View.VISIBLE);
                    updateAvailable(updateDetails);
                  /*  knowAboutDevice();*/
                }

            }
        });


        updateViewModel.downloadedBytes.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                mtvDown.setText(Extensions.bytesToMBFormatted(aLong, 2));
            }
        });

        updateViewModel.etaProgress.observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long aLong) {
                String time = convertMillisToTime(aLong);
                mtvDownPercent.setText(time + " remaning");

            }
        });

        updateViewModel.downloadProgress.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer progress) {
                onDownload();
                piDownload.setProgress(progress);

                if (progress == 100) {
                    fileDownloaded();
                }
            }
        });
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDownloadClick();
            }
        });
    }

    public void knowAboutDevice() {
        DeviceInfoUtil deviceInfoUtil = new DeviceInfoUtil(HomeActivity2.this, sharedPreferences);
        Log.d("DeviceInfoUtil", "SYS UPDATER APP VERSION " + deviceInfoUtil.getAppVersion());
        Log.d("DeviceInfoUtil", "DEVICE WONDEROS VERSION " + deviceInfoUtil.getOSVersion());
     /*   Log.d("DeviceInfoUtil","DEVICE IMEI NUMBER"+deviceInfoUtil.getIMEI());
        Log.d("DeviceInfoUtil","DEVICE SERIAL NUMBER"+deviceInfoUtil.getSerialNumber());
     */   Log.d("DeviceInfoUtil", "DEVICE MODEL NUMBER " + deviceInfoUtil.getModel());
        Log.d("DeviceInfoUtil", "DEVICE CURR DATE TIME " + deviceInfoUtil.getCurrentDateTimeGMT());
        Log.d("DeviceInfoUtil", "DEVICE UPDATE CHANNEL" + deviceInfoUtil.getUpdateChannelName());
        Log.d("DeviceInfoUtil", "DEVICE UPDATE VERSION " + deviceInfoUtil.getUpdateVersionNumber());

    }

    public String convertMillisToTime(long millis) {
        try {
            long seconds = millis / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            seconds = seconds % 60;
            minutes = minutes % 60;

            Log.d("RemainingTime", String.valueOf(millis));

            if (hours > 0) {
                return hours == 1 ? "1 hour" : hours + " hours";
            } else if (minutes > 0) {
                return minutes == 1 ? "1 minute" : minutes + " minutes";
            } else {
                return seconds == 1 ? "1 second" : seconds + " seconds";
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_SOFT_LEFT && event.getAction() == KeyEvent.ACTION_UP) {
            if (btnDownload.getVisibility() == View.VISIBLE) {
                onDownloadClick();

            } else if (btnCheckUpdates.getVisibility() == View.VISIBLE) {
                if (!Extensions.isInternetAvailable(this)) {
                    Extensions.toast(this, "No Internet Connection");
                    return false;
                }
                updateViewModel.fetchLatestUpdate();
            }
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (simplePopup == null) {
                showCustomPopup(btnMore.getRootView());
                Log.d("KeyEvent", "Menu button is showing the popup");
            } else {
                simplePopup.dismiss();
                // This should set simplePopup to null via the OnDismissListener
                Log.d("KeyEvent", "Menu button is dismissing the popup");
            }
            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_SOFT_RIGHT && event.getAction() == KeyEvent.ACTION_UP) {
            if (btnInstall.getVisibility() == View.VISIBLE) {
                onInstall();
            }
            if (btnReboot.getVisibility() == View.VISIBLE) {
                onReboot();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void updateAvailable(UpdateDetails updateDetails) {
        this.updateDetails = updateDetails;

        btnCheckUpdates.setVisibility(View.GONE);
        btnDownload.setVisibility(View.VISIBLE);
        updateTips.setVisibility(View.GONE);
        mtvVerTitle.setText("WonderOS " + updateDetails.getVersionNumber());
        mtvUpdateMsg.setText(getString(R.string.update_available));
        download.setVisibility(View.VISIBLE);
        mtvDesc1.setText(updateDetails.getDescription());
        mtvVerSize.setText(updateDetails.getFileSize() + "MB");
    }


    private void onDownload() {
        UpdateDetails updateDetails = sharedPreferences.getUpdateDetails();
        mtvVerSize.setVisibility(View.GONE);
        pbDownload.setVisibility(View.VISIBLE);
        mtvTtlDown.setVisibility(View.VISIBLE);
        mtvDown.setVisibility(View.VISIBLE);
        if (updateDetails != null) {
            mtvVerTitle.setText("WonderOS " + updateDetails.getVersionNumber());
            mtvDesc1.setText(updateDetails.getDescription());
        }
        if (Extensions.isInternetAvailable(this) && updateDetails != null) {
            // mtvDownloading.setText("Downloading...");
            mtvUpdateMsg.setText("Downloading...");
            if (mtvTtlDown.getVisibility() != View.VISIBLE) {
                mtvTtlDown.setVisibility(View.VISIBLE);
            }
            mtvTtlDown.setText(" / " + updateDetails.getFileSize() + "MB");
            mtvVerTitle.setText("WonderOS " + updateDetails.getVersionNumber());
            mtvDesc1.setText(updateDetails.getDescription());
        } else {
            mtvTtlDown.setVisibility(View.GONE);
        }
        download.setVisibility(View.VISIBLE);
        updateTips.setVisibility(View.GONE);
        btnCheckUpdates.setVisibility(View.GONE);
        btnDownload.setVisibility(View.GONE);
    }

    private void fileDownloaded() {
        if (updateViewModel.updateDetails.getValue() != null) {
            btnInstall.setVisibility(View.VISIBLE);
            btnDownload.setVisibility(View.GONE);
            pbDownload.setVisibility(View.GONE);
            btnCheckUpdates.setVisibility(View.GONE);
            mtvVerSize.setText(updateViewModel.updateDetails.getValue().getFileSize() + "MB");
            mtvUpdateMsg.setText("Update Available to install");
            mtvVerTitle.setText("WonderOS " + updateViewModel.updateDetails.getValue().getVersionNumber());
            mtvVerSize.setText(updateViewModel.updateDetails.getValue().getFileSize() + "MB");
            mtvDesc1.setText(updateViewModel.updateDetails.getValue().getDescription());
        }
    }

    public String extractFileNameFromUrl(String url) {
        // Check for null or empty URL
        if (url == null || url.isEmpty()) {
            return "default.zip";
        }
        // Remove any query parameters that might exist in the URL
        int queryParamIndex = url.indexOf('?');
        if (queryParamIndex > -1) {
            url = url.substring(0, queryParamIndex);
        }
        // Find the last occurrence of '/' and extract the substring after it
        int lastSlashIndex = url.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < url.length() - 1) {
            return url.substring(lastSlashIndex + 1);
        }
        // If there's no '/' or it's the last character, return a default filename
        return "default.zip";
    }

    private void onDownloadClick() {
        if (!Extensions.isInternetAvailable(HomeActivity2.this)) {
            Extensions.toast(HomeActivity2.this, "No Internet Connection");
            return;
        }
        String url = Constants.SERVER_URL + updateDetails.getUpdateFile();

        mtvUpdateMsg2.setVisibility(View.GONE);
        Intent intent = new Intent(HomeActivity2.this, DownloadService.class);
        intent.putExtra("url", url);
        intent.putExtra("fileName", "WonderOS " + updateDetails.getVersionNumber());
        startForegroundService(intent);
    }

    private void onInstall() {


        mtvTtlDown.setVisibility(View.GONE);
        mtvDown.setVisibility(View.GONE);
        String configFile = extractFileNameFromUrl(Constants.SERVER_URL + sharedPreferences.getUpdateDetails().getUpdateFile());
        List<UpdateConfig> mConfigs = UpdateConfigs.getUpdateConfigs(HomeActivity2.this);
        UpdateConfig applyConfig = null;
        for (int i = 0; i < mConfigs.size(); i++) {
            if (extractFileNameFromUrl(mConfigs.get(i).getUrl()).equals(configFile)) {
                applyConfig = mConfigs.get(i);
            }
        }
        if (applyConfig == null) {
            Extensions.toast(this, "Ota not allowed because config mismatched on server");
        } else {
            applyUpdate(applyConfig);
        }
    }

    private void sharedPrefDownloaded() {
        if (updateDetails != null) {
            sv1.setVisibility(View.VISIBLE);
            rlBottom.setVisibility(View.VISIBLE);
            download.setVisibility(View.VISIBLE);
            progressCircular.setVisibility(View.GONE);
            btnDownload.setVisibility(View.GONE);
            btnCheckUpdates.setVisibility(View.GONE);
            btnInstall.setVisibility(View.VISIBLE);
            pbDownload.setVisibility(View.GONE);
            mtvUpdateMsg.setText("Update Available to install");
            updateTips.setVisibility(View.GONE);
            mtvVerTitle.setText("WonderOS " + updateDetails.getVersionNumber());
            mtvDesc1.setText(updateDetails.getDescription());
            mtvVerSize.setText(updateDetails.getFileSize() + "MB");
        }
    }

    private Boolean isDownloadCompleted() {
        return sharedPreferences.isDownloadCompleted();
    }

    private Boolean isRebootRequired() {
        return sharedPreferences.isRebootRequired();
    }

    private void registerNetworkReceiver() {
        networkChangeReceiver = new NetworkChangeReceiver(isConnected -> {
            if (isConnected) {
                if (isFileCurrentlyDownloading()) {
                    mtvUpdateMsg.setText("Downloading...");
                } else {
                    updateViewModel.fetchLatestUpdate();
                }
            } else {
                if (isFileCurrentlyDownloading() || !isDownloadCompleted()) {
                    onInternetLost();
                    // mtvUpdateMsg.setText("Waiting for Network");
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);
    }

    private Boolean isFileCurrentlyDownloading() {
        if (updateViewModel.downloadedBytes.getValue() == null) {
            return false;
        } else {
            return (updateViewModel.downloadedBytes.getValue() > 0 && updateViewModel.downloadProgress.getValue() < 99.99);
        }
    }

    private void showCustomPopup(View anchor) {
        if (simplePopup != null) {
            simplePopup.dismiss();
            simplePopup = null;
            return;
        }
        List<SimplePopupValue> item = new ArrayList<SimplePopupValue>();
        item.add(new SimplePopupValue("Settings", R.drawable.ic_settings));
        simplePopup = new SimplePopup(this, item, new SimplePopup.PopupMenuItemListener() {
            @Override
            public void onMenuItemClick(View view, SimplePopupValue value, int position) {
                Intent intent = new Intent(HomeActivity2.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
        simplePopup.showAtLocation(anchor, Gravity.END | Gravity.BOTTOM, 30, 115);
    }
}