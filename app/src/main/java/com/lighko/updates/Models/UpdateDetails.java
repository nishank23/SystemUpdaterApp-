package com.lighko.updates.Models;

public class  UpdateDetails {
    private String _id;
    private String versionNumber;
    private String description;
    private String updateFile;
    private String createdAt;
    private int __v;
    private String fileSize;
    private String configFile;
    private String channelName;


    public UpdateDetails(String _id, String versionNumber, String description, String updateFile, String createdAt, int __v, String fileSize,String configFile,String channelName) {
        this._id = _id;
        this.versionNumber = versionNumber;
        this.description = description;
        this.updateFile = updateFile;
        this.createdAt = createdAt;
        this.__v = __v;
        this.fileSize = fileSize;
        this.configFile = configFile;
        this.channelName = channelName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUpdateFile() {
        return updateFile;
    }

    public void setUpdateFile(String updateFile) {
        this.updateFile = updateFile;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getV() {
        return __v;
    }

    public void setV(int __v) {
        this.__v = __v;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }
}
