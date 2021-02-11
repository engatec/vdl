package com.github.engatec.vdl.core.preferences;

import java.util.prefs.Preferences;

import com.github.engatec.vdl.Main;

public class ConfigManager {

    public static final ConfigManager INSTANCE = new ConfigManager();

    private final Preferences preferences;

    private ConfigManager() {
        preferences = Preferences.userNodeForPackage(Main.class);
    }

    public String getValue(ConfigProperty prop) {
        return preferences.get(prop.getKey(), prop.getDefaultValue());
    }

    public void setValue(ConfigProperty prop, String value) {
        preferences.put(prop.getKey(), value);
    }

    public void setValue(String key, String value) {
        preferences.put(key, value);
    }
}
