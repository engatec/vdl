package com.github.engatec.vdl.preference;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.github.engatec.vdl.Main;
import com.github.engatec.vdl.preference.configitem.ConfigItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigManager {

    private static final Logger LOGGER = LogManager.getLogger(ConfigManager.class);

    public static final ConfigManager INSTANCE = new ConfigManager();

    private final Preferences preferences;

    private ConfigManager() {
        preferences = Boolean.parseBoolean(System.getProperty("app.portable")) ? new PortablePreferences() : Preferences.userNodeForPackage(Main.class);
    }

    public <T> T getValue(ConfigItem<T> configItem) {
        return configItem.getValue(preferences);
    }

    public <T> void setValue(ConfigItem<T> configItem, T value) {
        configItem.setValue(preferences, value);
    }

    public String getValue(String key) {
        return preferences.get(key, null);
    }

    public void setValue(String key, String value) {
        preferences.put(key, value);
    }

    public void remove(String key) {
        preferences.remove(key);
    }

    public void flush() {
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            LOGGER.warn("Couldn't flush preferences", e);
        }
    }
}
