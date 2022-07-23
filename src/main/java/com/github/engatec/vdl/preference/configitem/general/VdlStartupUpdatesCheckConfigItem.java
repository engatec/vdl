package com.github.engatec.vdl.preference.configitem.general;

import java.util.prefs.Preferences;

public class VdlStartupUpdatesCheckConfigItem extends GeneralConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "vdl_startup_updates_check";
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
