package com.github.engatec.vdl.preference.configitem.misc;

import java.util.prefs.Preferences;

public class MultiSearchConfigItem extends MiscConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "multi_search";
    }

    @Override
    public Boolean getValue(Preferences prefs) {
        return prefs.getBoolean(getKey(), false);
    }

    @Override
    public void setValue(Preferences prefs, Boolean value) {
        if (value == null) {
            prefs.remove(getKey());
        } else {
            prefs.putBoolean(getKey(), value);
        }
    }
}
