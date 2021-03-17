package com.github.engatec.vdl.model.preferences.propertywrapper;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.preferences.ConfigItem;

public abstract class ConfigItemPropertyWrapper<P, V> {

    protected abstract ConfigItem<V> getConfigItem();

    public abstract P getProperty();

    public abstract V getValue();

    public abstract void setValue(V v);

    public void restore() {
        V value = ConfigManager.INSTANCE.getValue(getConfigItem());
        setValue(value);
    }

    public void save() {
        ConfigManager.INSTANCE.setValue(getConfigItem(), getValue());
    }
}
