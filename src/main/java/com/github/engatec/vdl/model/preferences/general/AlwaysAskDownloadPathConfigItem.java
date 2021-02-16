package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

public class AlwaysAskDownloadPathConfigItem extends GeneralConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "alwaysAskDownloadPath";
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
