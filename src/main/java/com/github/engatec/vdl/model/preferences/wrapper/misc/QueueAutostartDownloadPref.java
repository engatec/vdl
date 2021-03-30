package com.github.engatec.vdl.model.preferences.wrapper.misc;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.misc.QueueAutostartDownloadConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class QueueAutostartDownloadPref extends ConfigItemWrapper<BooleanProperty, Boolean> {

    private static final ConfigItem<Boolean> CONFIG_ITEM = new QueueAutostartDownloadConfigItem();

    private final BooleanProperty property = new SimpleBooleanProperty();

    public QueueAutostartDownloadPref() {
        restore();
        property.addListener((observable, oldValue, newValue) -> QueueAutostartDownloadPref.this.save());
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
