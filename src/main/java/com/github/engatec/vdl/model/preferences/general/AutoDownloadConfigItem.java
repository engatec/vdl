package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

public class AutoDownloadConfigItem extends GeneralConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "autodownload";
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
