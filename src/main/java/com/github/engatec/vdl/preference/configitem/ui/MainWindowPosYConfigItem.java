package com.github.engatec.vdl.preference.configitem.ui;

import java.util.prefs.Preferences;

public class MainWindowPosYConfigItem extends UiConfigItem<Double> {

    private static final double DEFAULT = -1;

    @Override
    protected String getName() {
        return "main_window_pos_y";
    }

    @Override
    public Double getValue(Preferences prefs) {
        return prefs.getDouble(getKey(), DEFAULT);
    }

    @Override
    public void setValue(Preferences prefs, Double value) {
        prefs.putDouble(getKey(), value);
    }
}
