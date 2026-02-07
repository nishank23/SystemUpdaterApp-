package com.lighko.updates.Adapters;

public class SimplePopupValue {
    private String title;
    private int resId;

    public SimplePopupValue(String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public int getResId() {
        return resId;
    }
}
