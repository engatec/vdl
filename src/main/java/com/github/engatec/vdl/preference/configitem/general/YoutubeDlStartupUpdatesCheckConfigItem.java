package com.github.engatec.vdl.preference.configitem.general;

import java.util.prefs.Preferences;

public class YoutubeDlStartupUpdatesCheckConfigItem extends GeneralConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "youtube_dl_startup_updates_check";
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
