package com.github.engatec.vdl.preference.property.misc;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.misc.RecentDownloadPathConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RecentDownloadPathConfigProperty extends ConfigProperty<StringProperty, String> {

    private static final ConfigItem<String> CONFIG_ITEM = new RecentDownloadPathConfigItem();

    private final StringProperty property = new SimpleStringProperty();

    public RecentDownloadPathConfigProperty() {
        restore();
        property.addListener((observable, oldValue, newValue) -> RecentDownloadPathConfigProperty.this.save());
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
