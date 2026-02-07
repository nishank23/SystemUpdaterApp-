package com.lighko.updates.Utils;

import android.content.Context;
import java.io.File;

public class FileUtils {

    // Call this method to delete all files in the getFilesDir() directory
    public static void clearFilesDir(Context context) {
        // Get the files directory
        File filesDir = context.getFilesDir();
        
        // Ensure the directory exists
        if (filesDir != null && filesDir.isDirectory()) {
            // List all files and directories in the files directory
            File[] files = filesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Delete each file or directory recursively
                    deleteRecursively(file);
                }
            }
        }
    }

    // Helper method to delete files and directories recursively
    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            // Recursively delete contents of the directory
            File[] files = file.listFiles();
            if (files != null) {
                for (File child : files) {
                    deleteRecursively(child);
                }
            }
        }
        // Delete the file or empty directory
        file.delete();
    }
}
