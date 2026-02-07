package com.lighko.updates.Utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class ConfigGenerator {

    public static void generateConfigFile(Context context, String nameVisibleUI, String fileUrl, String installType, String buildName, boolean forceSwitchSlot, boolean verifyPayloadMetadata, List<PropertyFile> propertyFiles) {
        try {
            JSONObject config = new JSONObject();
            config.put("__name", nameVisibleUI);
            config.put("__url", fileUrl);
            config.put("__ab_install_type", installType);
            config.put("name", buildName);
            config.put("url", fileUrl);
            config.put("ab_install_type", "NON_STREAMING");

            JSONObject abConfig = new JSONObject();

            abConfig.put("__", "A/B (seamless) update configurations");
            abConfig.put("__force_switch_slot", forceSwitchSlot);
            abConfig.put("force_switch_slot", forceSwitchSlot);
            abConfig.put("verify_payload_metadata", verifyPayloadMetadata);

            // Add property_files array
            JSONArray propertyFilesArray = new JSONArray();
            for (PropertyFile file : propertyFiles) {
                JSONObject fileObject = new JSONObject();
                fileObject.put("filename", file.filename);
                fileObject.put("offset", file.offset);
                fileObject.put("size", file.size);
                propertyFilesArray.put(fileObject);
            }
            abConfig.put("property_files", propertyFilesArray);

            config.put("ab_config", abConfig);

            // Ensure the directory exists
            File configDir = new File(context.getFilesDir(), "configs");
            if (!configDir.exists()) {
                configDir.mkdirs(); // Create the directory if it doesn't exist
            }

            File file = new File(configDir, buildName + "_config.json");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(config.toString(4));  // Indentation for readability
            fileWriter.close();

            Log.d("ConfigGenerator", "Config file has been successfully written to " + file.getPath());
        } catch (Exception e) {
            Log.e("ConfigGenerator", "Error writing config file", e);
        }
    }
}
