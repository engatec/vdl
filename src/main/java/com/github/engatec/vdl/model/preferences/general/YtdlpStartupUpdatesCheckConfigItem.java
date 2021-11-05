package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

public class YtdlpStartupUpdatesCheckConfigItem extends GeneralConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "yt_dlp_startup_updates_check";
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
