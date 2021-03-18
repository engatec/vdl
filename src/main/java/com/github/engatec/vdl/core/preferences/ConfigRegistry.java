package com.github.engatec.vdl.core.preferences;

import java.util.HashMap;
import java.util.Map;

import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSearchFromClipboardPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.model.preferences.wrapper.general.SkipDownloadableDetailsSearchPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;

public class ConfigRegistry {

    private static final Map<Class<? extends ConfigItemWrapper<?, ?>>, ConfigItemWrapper<?, ?>> REGISTRY = new HashMap<>();

    static {
        /* General */
        REGISTRY.put(AlwaysAskDownloadPathPref.class, new AlwaysAskDownloadPathPref());
        REGISTRY.put(AutoDownloadFormatPref.class, new AutoDownloadFormatPref());
        REGISTRY.put(AutoDownloadPref.class, new AutoDownloadPref());
        REGISTRY.put(AutoSearchFromClipboardPref.class, new AutoSearchFromClipboardPref());
        REGISTRY.put(DownloadPathPref.class, new DownloadPathPref());
        REGISTRY.put(LanguagePref.class, new LanguagePref());
        REGISTRY.put(SkipDownloadableDetailsSearchPref.class, new SkipDownloadableDetailsSearchPref());

        /* YoutubeDl */
        REGISTRY.put(ConfigFilePathPref.class, new ConfigFilePathPref());
        REGISTRY.put(NoMTimePref.class, new NoMTimePref());
        REGISTRY.put(ProxyUrlPref.class, new ProxyUrlPref());
        REGISTRY.put(UseConfigFilePref.class, new UseConfigFilePref());
    }

    public static <T extends ConfigItemWrapper<?, ?>> T get(Class<T> clazz) {
        return (T) REGISTRY.get(clazz);
    }

    public static void restorePreviousValues() {
        for (ConfigItemWrapper<?, ?> value : REGISTRY.values()) {
            value.restore();
        }
    }

    public static void saveAll() {
        for (ConfigItemWrapper<?, ?> value : REGISTRY.values()) {
            value.save();
        }
    }
}
