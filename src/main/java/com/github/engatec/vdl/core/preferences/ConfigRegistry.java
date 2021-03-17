package com.github.engatec.vdl.core.preferences;

import java.util.HashMap;
import java.util.Map;

import com.github.engatec.vdl.model.preferences.propertywrapper.ConfigItemPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.AlwaysAskDownloadPathPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.AutoDownloadFormatPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.AutoDownloadPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.AutoSearchFromClipboardPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.DownloadPathPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.LanguagePropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.general.SkipDownloadableDetailsSearchPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.youtubedl.ConfigFilePathPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.youtubedl.NoMTimePropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.youtubedl.ProxyUrlPropertyWrapper;
import com.github.engatec.vdl.model.preferences.propertywrapper.youtubedl.UseConfigFilePropertyWrapper;

public class ConfigRegistry {

    private static final Map<Class<? extends ConfigItemPropertyWrapper<?, ?>>, ConfigItemPropertyWrapper<?, ?>> REGISTRY = new HashMap<>();

    static {
        /* General */
        REGISTRY.put(AlwaysAskDownloadPathPropertyWrapper.class, new AlwaysAskDownloadPathPropertyWrapper());
        REGISTRY.put(AutoDownloadFormatPropertyWrapper.class, new AutoDownloadFormatPropertyWrapper());
        REGISTRY.put(AutoDownloadPropertyWrapper.class, new AutoDownloadPropertyWrapper());
        REGISTRY.put(AutoSearchFromClipboardPropertyWrapper.class, new AutoSearchFromClipboardPropertyWrapper());
        REGISTRY.put(DownloadPathPropertyWrapper.class, new DownloadPathPropertyWrapper());
        REGISTRY.put(LanguagePropertyWrapper.class, new LanguagePropertyWrapper());
        REGISTRY.put(SkipDownloadableDetailsSearchPropertyWrapper.class, new SkipDownloadableDetailsSearchPropertyWrapper());

        /* YoutubeDl */
        REGISTRY.put(ConfigFilePathPropertyWrapper.class, new ConfigFilePathPropertyWrapper());
        REGISTRY.put(NoMTimePropertyWrapper.class, new NoMTimePropertyWrapper());
        REGISTRY.put(ProxyUrlPropertyWrapper.class, new ProxyUrlPropertyWrapper());
        REGISTRY.put(UseConfigFilePropertyWrapper.class, new UseConfigFilePropertyWrapper());
    }

    public static <T extends ConfigItemPropertyWrapper<?, ?>> T get(Class<T> propertyWrapperClass) {
        return (T) REGISTRY.get(propertyWrapperClass);
    }

    public static void restorePreviousValues() {
        for (ConfigItemPropertyWrapper<?, ?> value : REGISTRY.values()) {
            value.restore();
        }
    }

    public static void saveAll() {
        for (ConfigItemPropertyWrapper<?, ?> value : REGISTRY.values()) {
            value.save();
        }
    }
}
