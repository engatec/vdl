package com.github.engatec.vdl.preference.property.youtubedl;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.youtubedl.NetrcConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class NetrcConfigProperty extends ConfigProperty<BooleanProperty, Boolean> {

    private static final ConfigItem<Boolean> CONFIG_ITEM = new NetrcConfigItem();

    private final BooleanProperty property = new SimpleBooleanProperty();

    public NetrcConfigProperty() {
        restore();
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
        return property.get();
    }

    @Override
    public void setValue(Boolean value) {
        property.set(value);
    }
}
