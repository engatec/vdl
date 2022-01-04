package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

public class DownloadThreadsConfigItem extends GeneralConfigItem<Integer> {

    private static final Integer DEFAULT = 3;

    @Override
    protected String getName() {
        return "download_threads";
    }

    @Override
    public Integer getValue(Preferences prefs) {
        return prefs.getInt(getKey(), DEFAULT);
    }

    @Override
    public void setValue(Preferences prefs, Integer value) {
        prefs.putInt(getKey(), value);
    }
}
