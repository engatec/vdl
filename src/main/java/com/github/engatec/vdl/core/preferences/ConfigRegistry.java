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
import com.github.engatec.vdl.model.preferences.wrapper.misc.QueueAutostartDownloadPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NetrcPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.TwoFactorCodePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.VideoPasswordPref;

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
        REGISTRY.put(SocketTimeoutPref.class, new SocketTimeoutPref());
        REGISTRY.put(UseConfigFilePref.class, new UseConfigFilePref());
        REGISTRY.put(SourceAddressPref.class, new SourceAddressPref());
        REGISTRY.put(ForceIpV4Pref.class, new ForceIpV4Pref());
        REGISTRY.put(ForceIpV6Pref.class, new ForceIpV6Pref());
        REGISTRY.put(AuthUsernamePref.class, new AuthUsernamePref());
        REGISTRY.put(AuthPasswordPref.class, new AuthPasswordPref());
        REGISTRY.put(TwoFactorCodePref.class, new TwoFactorCodePref());
        REGISTRY.put(NetrcPref.class, new NetrcPref());
        REGISTRY.put(VideoPasswordPref.class, new VideoPasswordPref());

        /* Misc */
        REGISTRY.put(QueueAutostartDownloadPref.class, new QueueAutostartDownloadPref());
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
