package com.github.engatec.vdl.model.preferences.wrapper.youtubedl;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import com.github.engatec.vdl.model.preferences.youtubedl.ForceIpV6ConfigItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class ForceIpV6Pref extends ConfigItemWrapper<BooleanProperty, Boolean> {

    private static final ConfigItem<Boolean> CONFIG_ITEM = new ForceIpV6ConfigItem();

    private final BooleanProperty property = new SimpleBooleanProperty();

    public ForceIpV6Pref() {
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
