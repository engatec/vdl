package com.github.engatec.vdl.model.preferences.wrapper.general;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.general.YtdlpStartupUpdatesCheckConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class YtdlpStartupUpdatesCheckPref extends ConfigItemWrapper<BooleanProperty, Boolean> {

    private static final ConfigItem<Boolean> CONFIG_ITEM = new YtdlpStartupUpdatesCheckConfigItem();

    private final BooleanProperty property = new SimpleBooleanProperty();

    public YtdlpStartupUpdatesCheckPref() {
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
