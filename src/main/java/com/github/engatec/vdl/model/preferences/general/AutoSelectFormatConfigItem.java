package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

public class AutoSelectFormatConfigItem extends GeneralConfigItem<Integer> {

    public static final Integer DEFAULT = -1;

    @Override
    protected String getName() {
        return "autoSelectFormat";
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
