package com.github.engatec.vdl.model.preferences.youtubedl;

import java.util.prefs.Preferences;

public class NoMTimeConfigItem extends YoutubeDlConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "noMTime";
    }

    @Override
    public Boolean getValue(Preferences prefs) {
        return prefs.getBoolean(getKey(), true);
    }

    @Override
    public void setValue(Preferences prefs, Boolean value) {
        prefs.putBoolean(getKey(), value);
    }
}
