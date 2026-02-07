package com.lighko.updates.Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtility {

    public static class HttpResponseException extends IOException {
        private int statusCode;
        public HttpResponseException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }
        public int getStatusCode() {
            return statusCode;
        }
    }

    public static String getJsonWithQueryParams(String urlString, Map<String, String> queryParams) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            StringBuilder urlWithParams = new StringBuilder(urlString);
            if (queryParams != null && !queryParams.isEmpty()) {
                urlWithParams.append("?");
                for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                    urlWithParams.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    urlWithParams.append("=");
                    urlWithParams.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                    urlWithParams.append("&");
                }
                urlWithParams.deleteCharAt(urlWithParams.length() - 1); // Remove the last "&"
            }

            URL url = new URL(urlWithParams.toString());
            Log.d("HttpUtility", "Connecting to URL: " + urlWithParams.toString());

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            int responseCode = connection.getResponseCode();
            Log.d("HttpUtility", "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } else {
                String errorMessage = connection.getResponseMessage();
                Log.e("HttpUtility", "Error Message: " + errorMessage);
                throw new HttpResponseException(responseCode, "HTTP error code: " + responseCode + " - " + errorMessage);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("HttpUtility", "Error closing reader", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }




    public static String postWithQueryParams(String urlString, Map<String, String> postParams) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();

        try {
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> entry : postParams.entrySet()) {
                postData.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                postData.append("=");
                postData.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                postData.append("&");
            }
            postData.deleteCharAt(postData.length() - 1); // Remove the last "&"

            byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);

            URL url = new URL(urlString);
            Log.d("HttpUtility", "Connecting to URL: " + urlString);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            connection.setDoOutput(true);
            connection.getOutputStream().write(postDataBytes);

            int responseCode = connection.getResponseCode();
            Log.d("HttpUtility", "Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } else {
                String errorMessage = connection.getResponseMessage();
                Log.e("HttpUtility", "Error Message: " + errorMessage);
                throw new HttpResponseException(responseCode, "HTTP error code: " + responseCode + " - " + errorMessage);
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("HttpUtility", "Error closing reader", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response.toString();
    }




}
