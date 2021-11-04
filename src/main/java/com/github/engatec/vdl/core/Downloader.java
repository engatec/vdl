package com.github.engatec.vdl.core;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public enum Downloader {

    YOUTUBE_DL(1, "youtube-dl") {
        @Override
        public String resolveFileName() {
            return StringUtils.defaultIfBlank(
                    System.getProperty("app.youtubedl"),
                    SystemUtils.IS_OS_WINDOWS ? "youtube-dl.exe" : "youtube-dl" // If app.youtubedl is not set, assume default name
            );
        }
    },

    YT_DLP(2, "yt-dlp") {
        @Override
        public String resolveFileName() {
            return StringUtils.defaultIfBlank(
                    System.getProperty("app.ytdlp"),
                    SystemUtils.IS_OS_WINDOWS ? "yt-dlp.exe" : "yt-dlp" // If app.ytdlp is not set, assume default name
            );
        }
    };

    private final int configValue;
    private final String displayValue;

    Downloader(int configValue, String displayValue) {
        this.configValue = configValue;
        this.displayValue = displayValue;
    }

    public abstract String resolveFileName();

    public int getConfigValue() {
        return configValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static Downloader getByConfigValue(int value) {
        for (Downloader item : values()) {
            if (item.configValue == value) {
                return item;
            }
        }

        throw new NoSuchElementException("No config value '" + value + "' exists");
    }

    public static Downloader getByDisplaValue(String value) {
        for (Downloader item : values()) {
            if (item.displayValue.equals(value)) {
                return item;
            }
        }

        throw new NoSuchElementException("No display value '" + value + "' exists");
    }
}
