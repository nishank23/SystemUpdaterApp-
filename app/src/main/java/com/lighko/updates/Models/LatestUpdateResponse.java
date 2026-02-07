package com.lighko.updates.Models;

public class LatestUpdateResponse {
    private String message;
    private UpdateDetails latestUpdate;

    public LatestUpdateResponse(String message, UpdateDetails latestUpdate) {
        this.message = message;
        this.latestUpdate = latestUpdate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UpdateDetails getLatestUpdate() {
        return latestUpdate;
    }

    public void setLatestUpdate(UpdateDetails latestUpdate) {
        this.latestUpdate = latestUpdate;
    }
}

