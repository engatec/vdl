package com.github.engatec.vdl.preference.property.ui;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.ui.MainWindowWidthConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class MainWindowWidthConfigProperty extends ConfigProperty<DoubleProperty, Double> {

    private static final ConfigItem<Double> CONFIG_ITEM = new MainWindowWidthConfigItem();

    private final DoubleProperty property = new SimpleDoubleProperty();

    public MainWindowWidthConfigProperty() {
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
