package com.github.engatec.vdl.model.preferences.wrapper.ui;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.ui.MainWindowPosYConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class MainWindowPosYPref extends ConfigItemWrapper<DoubleProperty, Double> {

    private static final ConfigItem<Double> CONFIG_ITEM = new MainWindowPosYConfigItem();

    private final DoubleProperty property = new SimpleDoubleProperty();

    public MainWindowPosYPref() {
        restore();
        property.addListener((observable, oldValue, newValue) -> save());
    }

    @Override
    protected ConfigItem<Double> getConfigItem() {
        return CONFIG_ITEM;
    }

    @Override
    public DoubleProperty getProperty() {
        return property;
    }

    @Override
    public Double getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(Double value) {
        property.setValue(value);
    }
}
