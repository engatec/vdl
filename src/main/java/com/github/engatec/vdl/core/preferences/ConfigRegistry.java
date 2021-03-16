package com.github.engatec.vdl.core.preferences;

import java.util.HashMap;
import java.util.Map;

import com.github.engatec.vdl.model.preferences.propertywrapper.ConfigItemPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.AlwaysAskDownloadPathPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.AutoDownloadFormatPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.AutoDownloadPropertyWrapper;

public class ConfigRegistry {

    private static final Map<Class<? extends ConfigItemPropertyWrapper<?, ?>>, ConfigItemPropertyWrapper<?, ?>> REGISTRY = new HashMap<>();

    static {
        /* General */
        REGISTRY.put(AlwaysAskDownloadPathPropertyWrapper.class, new AlwaysAskDownloadPathPropertyWrapper());
        REGISTRY.put(AutoDownloadFormatPropertyWrapper.class, new AutoDownloadFormatPropertyWrapper());
        REGISTRY.put(AutoDownloadPropertyWrapper.class, new AutoDownloadPropertyWrapper());

        /* YoutubeDl */
    }

    public static <T extends ConfigItemPropertyWrapper<?, ?>> T get(Class<T> propertyWrapperClass) {
        return (T) REGISTRY.get(propertyWrapperClass);
    }

    public void storeAll() {
        for (ConfigItemPropertyWrapper<?, ?> value : REGISTRY.values()) {
            value.store();
        }
    }
}
