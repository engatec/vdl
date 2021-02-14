package com.github.engatec.vdl.model.preferences;

public enum ConfigCategory {

    GENERAL("general"),
    YOUTUBE_DL("youtubedl");

    private final String keyPrefix;

    ConfigCategory(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }
}
