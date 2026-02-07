package com.lighko.updates.services;

import com.lighko.updates.Models.UpdateDetails;
import com.lighko.updates.Network.HttpUtility;
import com.lighko.updates.Models.LatestUpdateResponse;
import com.lighko.updates.Utils.Constants;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdateServiceImpl {
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();


    public void getLatestUpdate(String buildNumber, String serialNumber, Callback<LatestUpdateResponse> callback) {
        executor.execute(() -> {
            try {
                Map<String, String> queryParams = new HashMap<>();
                queryParams.put("buildNumber", buildNumber);
                queryParams.put("serialNumber", serialNumber);

                String url = Constants.SERVER_URL + "api/v1/update";

                // Make the HTTP request
                String jsonResponse = HttpUtility.getJsonWithQueryParams(url, queryParams);

                // Parse the response
                LatestUpdateResponse response = parseJson(jsonResponse);
                callback.onResponse(response);

            } catch (Exception e) {
                if (e instanceof HttpUtility.HttpResponseException) {
                    HttpUtility.HttpResponseException httpException = (HttpUtility.HttpResponseException) e;
                    int statusCode = httpException.getStatusCode();
                    if (statusCode == 404) {
                        callback.onMessage("No updates found.");
                    } else {
                        callback.onFailure(e);
                    }
                } else {
                    callback.onFailure(e);
                }
            }
        });
    }

        private LatestUpdateResponse parseJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        String message = jsonObject.getString("message");
        JSONObject updateDetailsJson = jsonObject.getJSONObject("latestUpdate");

        UpdateDetails updateDetails = new UpdateDetails(
            updateDetailsJson.getString("_id"),
            updateDetailsJson.getString("versionNumber"),
            updateDetailsJson.getString("description"),
            updateDetailsJson.getString("updateFile"),
            updateDetailsJson.getString("createdAt"),
            updateDetailsJson.getInt("__v"),
            updateDetailsJson.getString("fileSize"),
            updateDetailsJson.getString("configFile"),
            updateDetailsJson.getString("channelName")
        );

        return new LatestUpdateResponse(message, updateDetails);
    }
    public interface Callback<T> {
        void onResponse(T response);
        void onFailure(Exception e);
        void onMessage(String message);
    }
}
