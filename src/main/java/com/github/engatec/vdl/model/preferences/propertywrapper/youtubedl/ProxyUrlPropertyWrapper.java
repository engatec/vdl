package com.github.engatec.vdl.model.preferences.propertywrapper.youtubedl;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.propertywrapper.ConfigItemPropertyWrapper;
import com.github.engatec.vdl.model.preferences.youtubedl.ProxyUrlConfigItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ProxyUrlPropertyWrapper extends ConfigItemPropertyWrapper<StringProperty, String> {

    private static final ConfigItem<String> CONFIG_ITEM = new ProxyUrlConfigItem();

    private final StringProperty property = new SimpleStringProperty();

    public ProxyUrlPropertyWrapper() {
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
