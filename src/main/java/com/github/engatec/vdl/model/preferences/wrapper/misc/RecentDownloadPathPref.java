package com.github.engatec.vdl.model.preferences.wrapper.misc;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.misc.RecentDownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RecentDownloadPathPref extends ConfigItemWrapper<StringProperty, String> {

    private static final ConfigItem<String> CONFIG_ITEM = new RecentDownloadPathConfigItem();

    private final StringProperty property = new SimpleStringProperty();

    public RecentDownloadPathPref() {
        restore();
        property.addListener((observable, oldValue, newValue) -> RecentDownloadPathPref.this.save());
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
        return property.getValue();
    }

    @Override
    public void setValue(String value) {
        property.setValue(value);
    }
}
