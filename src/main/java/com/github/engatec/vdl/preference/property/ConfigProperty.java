package com.github.engatec.vdl.preference.property;

import com.github.engatec.vdl.preference.ConfigManager;
import com.github.engatec.vdl.preference.configitem.ConfigItem;
import javafx.beans.property.Property;

public abstract class ConfigProperty<P extends Property<?>, V> {

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
