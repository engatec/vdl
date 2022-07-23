package com.github.engatec.vdl.preference.property.misc;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.misc.MultiSearchConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MultiSearchConfigProperty extends ConfigProperty<BooleanProperty, Boolean> {

    private static final ConfigItem<Boolean> CONFIG_ITEM = new MultiSearchConfigItem();

    private final BooleanProperty property = new SimpleBooleanProperty();

    public MultiSearchConfigProperty() {
        restore();
        property.addListener((observable, oldValue, newValue) -> MultiSearchConfigProperty.this.save());
    }

    @Override
    protected ConfigItem<Boolean> getConfigItem() {
        return CONFIG_ITEM;
    }

    @Override
    public BooleanProperty getProperty() {
        return property;
    }

    @Override
    public Boolean getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(Boolean value) {
        property.setValue(value);
    }
}
