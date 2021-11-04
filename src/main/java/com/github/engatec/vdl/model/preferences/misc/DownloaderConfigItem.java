package com.github.engatec.vdl.model.preferences.misc;

import java.util.prefs.Preferences;

import com.github.engatec.vdl.core.Downloader;

public class DownloaderConfigItem extends MiscConfigItem<Integer> {

    private static final int DEFAULT = Downloader.YOUTUBE_DL.getConfigValue();

    @Override
    protected String getName() {
        return "downloader";
    }

    @Override
    public Integer getValue(Preferences prefs) {
        return prefs.getInt(getKey(), DEFAULT);
    }

    @Override
    public void setValue(Preferences prefs, Integer value) {
        if (value == null) {
            prefs.remove(getKey());
        } else {
            prefs.putInt(getKey(), value);
        }
    }
}
