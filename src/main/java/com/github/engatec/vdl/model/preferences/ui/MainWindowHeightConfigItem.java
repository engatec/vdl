package com.github.engatec.vdl.model.preferences.ui;

import java.util.prefs.Preferences;

public class MainWindowHeightConfigItem extends UiConfigItem<Double> {

    private static final double DEFAULT = 600;

    @Override
    protected String getName() {
        return "main_window_height";
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
