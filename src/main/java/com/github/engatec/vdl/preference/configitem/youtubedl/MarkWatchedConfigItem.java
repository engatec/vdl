package com.github.engatec.vdl.preference.configitem.youtubedl;

import java.util.prefs.Preferences;

public class MarkWatchedConfigItem extends YoutubeDlConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "markWatched";
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
