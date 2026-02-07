package com.lighko.updates.Models;

import android.graphics.drawable.Drawable;

public class SettingItem {
    private String title;
    private String description;
    private boolean isChecked;
    private Drawable icon;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public SettingItem(String title, String description, boolean isChecked, Drawable icon) {
        this.title = title;
        this.description = description;
        this.isChecked = isChecked;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
