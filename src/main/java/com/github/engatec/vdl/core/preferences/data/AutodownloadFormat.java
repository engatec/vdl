package com.github.engatec.vdl.core.preferences.data;

public class AutodownloadFormat {

    private final String title;
    private final String value;

    public AutodownloadFormat(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return title;
    }
}
