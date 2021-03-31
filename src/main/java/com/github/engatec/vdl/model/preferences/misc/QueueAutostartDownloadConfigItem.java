package com.github.engatec.vdl.model.preferences.misc;

import java.util.prefs.Preferences;

public class QueueAutostartDownloadConfigItem extends MiscConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "queueAutostartDownload";
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
