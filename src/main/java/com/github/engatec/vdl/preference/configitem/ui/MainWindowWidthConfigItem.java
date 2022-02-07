package com.github.engatec.vdl.preference.configitem.ui;

import java.util.prefs.Preferences;

public class MainWindowWidthConfigItem extends UiConfigItem<Double> {

    private static final double DEFAULT = 800;

    @Override
    protected String getName() {
        return "main_window_width";
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
