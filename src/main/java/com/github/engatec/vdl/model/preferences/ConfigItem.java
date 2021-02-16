package com.github.engatec.vdl.model.preferences;

import java.util.prefs.Preferences;

public abstract class ConfigItem<T> {

    protected abstract String getCategory();
    protected abstract String getName();

    public abstract T getValue(Preferences prefs);
    public abstract void setValue(Preferences prefs, T value);

    public String getKey() {
        return getCategory() + "." + getName();
    }
}
