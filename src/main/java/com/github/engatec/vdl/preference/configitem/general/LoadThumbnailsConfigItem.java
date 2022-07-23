package com.github.engatec.vdl.preference.configitem.general;

import java.util.prefs.Preferences;

public class LoadThumbnailsConfigItem extends GeneralConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "load_thumbnails";
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
