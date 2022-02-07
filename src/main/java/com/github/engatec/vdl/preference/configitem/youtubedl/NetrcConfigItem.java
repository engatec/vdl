package com.github.engatec.vdl.preference.configitem.youtubedl;

import java.util.prefs.Preferences;

public class NetrcConfigItem extends YoutubeDlConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "netrc";
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
