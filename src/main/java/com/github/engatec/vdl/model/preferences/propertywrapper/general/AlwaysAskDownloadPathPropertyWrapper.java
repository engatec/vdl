package com.github.engatec.vdl.model.preferences.propertywrapper.general;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.general.AlwaysAskDownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.propertywrapper.ConfigItemPropertyWrapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class AlwaysAskDownloadPathPropertyWrapper extends ConfigItemPropertyWrapper<BooleanProperty, Boolean> {

    private static final ConfigItem<Boolean> CONFIG_ITEM = new AlwaysAskDownloadPathConfigItem();

    private final BooleanProperty property = new SimpleBooleanProperty();

    public AlwaysAskDownloadPathPropertyWrapper() {
        Boolean value = ConfigManager.INSTANCE.getValue(CONFIG_ITEM);
        property.setValue(value);
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
