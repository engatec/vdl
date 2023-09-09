package com.github.engatec.vdl.core;

import java.util.NoSuchElementException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public enum Engine {

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

    Engine(int configValue, String displayValue) {
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

    public static Engine getByConfigValue(int value) {
        for (Engine item : values()) {
            if (item.configValue == value) {
                return item;
            }
        }

        throw new NoSuchElementException("No config value '" + value + "' exists");
    }

    public static Engine getByDisplaValue(String value) {
        for (Engine item : values()) {
            if (item.displayValue.equals(value)) {
                return item;
            }
        }

        throw new NoSuchElementException("No display value '" + value + "' exists");
    }
}
