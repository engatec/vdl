package com.github.engatec.vdl.model.preferences.misc;

import java.util.prefs.Preferences;

public class HistoryEntriesNumberConfigItem extends MiscConfigItem<Integer> {

    private static final int DEFAULT_HISTORY_ENTRIES = 30;

    @Override
    protected String getName() {
        return "historyEntriesNumber";
    }

    @Override
    public Integer getValue(Preferences prefs) {
        return prefs.getInt(getKey(), DEFAULT_HISTORY_ENTRIES);
    }

    @Override
    public void setValue(Preferences prefs, Integer value) {
        prefs.putInt(getKey(), value);
    }
}
