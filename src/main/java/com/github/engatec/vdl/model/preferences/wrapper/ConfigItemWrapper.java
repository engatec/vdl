package com.github.engatec.vdl.model.preferences.wrapper;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.preferences.ConfigItem;
import javafx.beans.property.Property;

public abstract class ConfigItemWrapper<P extends Property<?>, V> {

    protected abstract ConfigItem<V> getConfigItem();

    public abstract P getProperty();

    public abstract V getValue();

    public abstract void setValue(V v);

    public void restore() {
        if (getProperty().isBound()) {
            return;
        }

        V value = ConfigManager.INSTANCE.getValue(getConfigItem());
        setValue(value);
    }

    public void save() {
        ConfigManager.INSTANCE.setValue(getConfigItem(), getValue());
    }
}
