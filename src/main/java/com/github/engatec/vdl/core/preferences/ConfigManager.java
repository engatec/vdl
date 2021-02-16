package com.github.engatec.vdl.core.preferences;

import java.util.prefs.Preferences;

import com.github.engatec.vdl.Main;
import com.github.engatec.vdl.model.preferences.ConfigItem;

public class ConfigManager {

    public static final ConfigManager INSTANCE = new ConfigManager();

    private final Preferences preferences;

    private ConfigManager() {
        preferences = Preferences.userNodeForPackage(Main.class);
    }

    public <T> T getValue(ConfigItem<T> configItem) {
        return configItem.getValue(preferences);
    }

    public <T> void setValue(ConfigItem<T> configItem, T value) {
        configItem.setValue(preferences, value);
    }

    public void setValue(String key, String value) {
        preferences.put(key, value);
    }
}
