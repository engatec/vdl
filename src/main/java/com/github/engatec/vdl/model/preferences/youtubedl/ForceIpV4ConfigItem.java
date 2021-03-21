package com.github.engatec.vdl.model.preferences.youtubedl;

import java.util.prefs.Preferences;

public class ForceIpV4ConfigItem extends YoutubeDlConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "forceIpV4";
    }

    @Override
    public Boolean getValue(Preferences prefs) {
        return prefs.getBoolean(getKey(), false);
    }

    @Override
    public void setValue(Preferences prefs, Boolean value) {
        prefs.putBoolean(getKey(), value);
    }
}
