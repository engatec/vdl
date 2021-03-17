package com.github.engatec.vdl.model.preferences.propertywrapper.general;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.general.DownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.propertywrapper.ConfigItemPropertyWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DownloadPathPropertyWrapper extends ConfigItemPropertyWrapper<StringProperty, String> {

    private static final ConfigItem<String> CONFIG_ITEM = new DownloadPathConfigItem();

    private final StringProperty property = new SimpleStringProperty();

    public DownloadPathPropertyWrapper() {
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
