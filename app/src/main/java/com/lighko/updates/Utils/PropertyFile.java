package com.lighko.updates.Utils;

public class PropertyFile {
    String filename;
    int offset;
    int size;

    public PropertyFile(String filename, int offset, int size) {
        this.filename = filename;
        this.offset = offset;
        this.size = size;
    }
}
