package com.lighko.updates.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadConfigFileTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private SharedPreferencesHelper sharedPreferences;

    public DownloadConfigFileTask(Context context, SharedPreferencesHelper sharedPreferences) {
        this.context = context.getApplicationContext(); // Use application context
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        File configDir = new File(context.getFilesDir(), "configs");
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        String configUrl = Constants.SERVER_URL + sharedPreferences.getUpdateDetails().getConfigFile();
        String configFile = extractFileNameFromUrl(configUrl);
        File destFile = new File(configDir, configFile);

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            URL url = new URL(configUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
            }

            inputStream = connection.getInputStream();
            outputStream = new FileOutputStream(destFile);

            byte[] data = new byte[4096];
            int count;
            while ((count = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
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


}
