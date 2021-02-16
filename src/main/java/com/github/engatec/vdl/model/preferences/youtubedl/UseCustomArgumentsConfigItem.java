package com.github.engatec.vdl.model.preferences.youtubedl;

import java.util.prefs.Preferences;

public class UseCustomArgumentsConfigItem extends YoutubeDlConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "useCustomArguments";
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
