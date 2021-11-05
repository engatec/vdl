package com.github.engatec.vdl.core.preferences;

import java.util.HashMap;
import java.util.Map;

import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AudioExtractionFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AudioExtractionQualityPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSearchFromClipboardPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSelectFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.model.preferences.wrapper.general.LoadThumbnailsPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YoutubeDlStartupUpdatesCheckPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YtdlpStartupUpdatesCheckPref;
import com.github.engatec.vdl.model.preferences.wrapper.misc.DownloaderPref;
import com.github.engatec.vdl.model.preferences.wrapper.misc.HistoryEntriesNumberPref;
import com.github.engatec.vdl.model.preferences.wrapper.misc.RecentDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.ui.MainWindowHeightPref;
import com.github.engatec.vdl.model.preferences.wrapper.ui.MainWindowPosXPref;
import com.github.engatec.vdl.model.preferences.wrapper.ui.MainWindowPosYPref;
import com.github.engatec.vdl.model.preferences.wrapper.ui.MainWindowWidthPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.CookiesFileLocationPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.MarkWatchedPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NetrcPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoContinuePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoPartPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.OutputTemplatePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.RateLimitPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ReadCookiesPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.TwoFactorCodePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.VideoPasswordPref;

public class ConfigRegistryImpl implements ConfigRegistry {

    private static final Map<Class<? extends ConfigItemWrapper<?, ?>>, ConfigItemWrapper<?, ?>> REGISTRY = new HashMap<>();

    static {
        /* General */
        addToRegistry(new AlwaysAskDownloadPathPref());
        addToRegistry(new AudioExtractionFormatPref());
        addToRegistry(new AudioExtractionQualityPref());
        addToRegistry(new AutoSelectFormatPref());
        addToRegistry(new AutoSearchFromClipboardPref());
        addToRegistry(new DownloadPathPref());
        addToRegistry(new LanguagePref());
        addToRegistry(new YoutubeDlStartupUpdatesCheckPref());
        addToRegistry(new YtdlpStartupUpdatesCheckPref());
        addToRegistry(new LoadThumbnailsPref());

        /* YoutubeDl / YtDlp */
        addToRegistry(new ConfigFilePathPref());
        addToRegistry(new OutputTemplatePref());
        addToRegistry(new MarkWatchedPref());
        addToRegistry(new NoContinuePref());
        addToRegistry(new NoPartPref());
        addToRegistry(new NoMTimePref());
        addToRegistry(new RateLimitPref());
        addToRegistry(new ReadCookiesPref());
        addToRegistry(new CookiesFileLocationPref());
        addToRegistry(new ProxyUrlPref());
        addToRegistry(new SocketTimeoutPref());
        addToRegistry(new UseConfigFilePref());
        addToRegistry(new SourceAddressPref());
        addToRegistry(new ForceIpV4Pref());
        addToRegistry(new ForceIpV6Pref());
        addToRegistry(new AuthUsernamePref());
        addToRegistry(new AuthPasswordPref());
        addToRegistry(new TwoFactorCodePref());
        addToRegistry(new NetrcPref());
        addToRegistry(new VideoPasswordPref());

        /* Misc */
        addToRegistry(new DownloaderPref());
        addToRegistry(new HistoryEntriesNumberPref());
        addToRegistry(new RecentDownloadPathPref());

        /* UI */
        addToRegistry(new MainWindowPosXPref());
        addToRegistry(new MainWindowPosYPref());
        addToRegistry(new MainWindowWidthPref());
        addToRegistry(new MainWindowHeightPref());
    }

    @SuppressWarnings("unchecked")
    private static <T extends ConfigItemWrapper<?, ?>> void addToRegistry(T pref) {
        REGISTRY.put((Class<? extends ConfigItemWrapper<?, ?>>) pref.getClass(), pref);
    }

    @SuppressWarnings("unchecked")
    public <T extends ConfigItemWrapper<?, ?>> T get(Class<T> clazz) {
        return (T) REGISTRY.get(clazz);
    }

    public void dropUnsavedChanges() {
        for (ConfigItemWrapper<?, ?> value : REGISTRY.values()) {
            value.restore();
        }
    }

    public void saveAll() {
        for (ConfigItemWrapper<?, ?> value : REGISTRY.values()) {
            value.save();
        }
    }
}
