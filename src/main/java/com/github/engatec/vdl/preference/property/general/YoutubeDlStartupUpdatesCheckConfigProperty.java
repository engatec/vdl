package com.github.engatec.vdl.preference.property.general;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.general.YoutubeDlStartupUpdatesCheckConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class YoutubeDlStartupUpdatesCheckConfigProperty extends ConfigProperty<BooleanProperty, Boolean> {

    private static final ConfigItem<Boolean> CONFIG_ITEM = new YoutubeDlStartupUpdatesCheckConfigItem();

    private final BooleanProperty property = new SimpleBooleanProperty();

    public YoutubeDlStartupUpdatesCheckConfigProperty() {
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