package com.github.engatec.vdl.core.preferences;

import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;

public interface ConfigRegistry {

    <T extends ConfigItemWrapper<?, ?>> T get(Class<T> clazz);

    void dropUnsavedChanges();

    void saveAll();
}
