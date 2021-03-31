package com.github.engatec.vdl.model.preferences.wrapper.youtubedl;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import com.github.engatec.vdl.model.preferences.youtubedl.TwoFactorCodeConfigItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TwoFactorCodePref extends ConfigItemWrapper<StringProperty, String> {

    private static final ConfigItem<String> CONFIG_ITEM = new TwoFactorCodeConfigItem();

    private final StringProperty property = new SimpleStringProperty();

    public TwoFactorCodePref() {
        restore();
    }

    @Override
    protected ConfigItem<String> getConfigItem() {
        return CONFIG_ITEM;
    }

    @Override
    public StringProperty getProperty() {
        return property;
    }

    @Override
    public String getValue() {
        return property.get();
    }

    @Override
    public void setValue(String value) {
        property.set(value);
    }
}
