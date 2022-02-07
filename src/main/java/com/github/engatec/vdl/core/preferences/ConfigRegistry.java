package com.github.engatec.vdl.core.preferences;

import com.github.engatec.vdl.preference.property.ConfigProperty;

public interface ConfigRegistry {

    <T extends ConfigProperty<?, ?>> T get(Class<T> clazz);

    void dropUnsavedChanges();

    void saveAll();
}
