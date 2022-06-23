package com.github.engatec.vdl.preference;

import java.util.HashMap;
import java.util.Map;

import com.github.engatec.vdl.preference.property.ConfigProperty;
import com.github.engatec.vdl.preference.property.general.AlwaysAskDownloadPathConfigProperty;
import com.github.engatec.vdl.preference.property.general.AudioExtractionFormatConfigProperty;
import com.github.engatec.vdl.preference.property.general.AudioExtractionQualityConfigProperty;
import com.github.engatec.vdl.preference.property.general.AutoSearchFromClipboardConfigProperty;
import com.github.engatec.vdl.preference.property.general.AutoSelectFormatConfigProperty;
import com.github.engatec.vdl.preference.property.general.DownloadPathConfigProperty;
import com.github.engatec.vdl.preference.property.general.DownloadThreadsConfigProperty;
import com.github.engatec.vdl.preference.property.general.LanguageConfigProperty;
import com.github.engatec.vdl.preference.property.general.LoadThumbnailsConfigProperty;
import com.github.engatec.vdl.preference.property.general.VdlStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.preference.property.general.YoutubeDlStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.preference.property.general.YtdlpStartupUpdatesCheckConfigProperty;
import com.github.engatec.vdl.preference.property.misc.DownloaderConfigProperty;
import com.github.engatec.vdl.preference.property.misc.HistoryEntriesNumberConfigProperty;
import com.github.engatec.vdl.preference.property.misc.HistoryTableConfigProperty;
import com.github.engatec.vdl.preference.property.misc.RecentDownloadPathConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowHeightConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowPosXConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowPosYConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowWidthConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.AuthPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.AuthUsernameConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ConfigFilePathConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.CookiesFileLocationConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.EmbedSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ForceIpV4ConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ForceIpV6ConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.MarkWatchedConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NetrcConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NoContinueConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NoMTimeConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.NoPartConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.OutputTemplateConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.PreferredSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ProxyEnabledConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ProxyUrlConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.RateLimitConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.ReadCookiesConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.SocketTimeoutConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.SourceAddressConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.TwoFactorCodeConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.UseConfigFileConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.VideoPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.WriteSubtitlesConfigProperty;

public class ConfigRegistryImpl implements ConfigRegistry {

    private static final Map<Class<? extends ConfigProperty<?, ?>>, ConfigProperty<?, ?>> REGISTRY = new HashMap<>();

    static {
        /* General */
        addToRegistry(new AlwaysAskDownloadPathConfigProperty());
        addToRegistry(new AudioExtractionFormatConfigProperty());
        addToRegistry(new AudioExtractionQualityConfigProperty());
        addToRegistry(new AutoSelectFormatConfigProperty());
        addToRegistry(new AutoSearchFromClipboardConfigProperty());
        addToRegistry(new DownloadPathConfigProperty());
        addToRegistry(new LanguageConfigProperty());
        addToRegistry(new DownloadThreadsConfigProperty());
        addToRegistry(new VdlStartupUpdatesCheckConfigProperty());
        addToRegistry(new YoutubeDlStartupUpdatesCheckConfigProperty());
        addToRegistry(new YtdlpStartupUpdatesCheckConfigProperty());
        addToRegistry(new LoadThumbnailsConfigProperty());

        /* YoutubeDl / YtDlp */
        addToRegistry(new ConfigFilePathConfigProperty());
        addToRegistry(new OutputTemplateConfigProperty());
        addToRegistry(new MarkWatchedConfigProperty());
        addToRegistry(new NoContinueConfigProperty());
        addToRegistry(new NoPartConfigProperty());
        addToRegistry(new NoMTimeConfigProperty());
        addToRegistry(new RateLimitConfigProperty());
        addToRegistry(new ReadCookiesConfigProperty());
        addToRegistry(new CookiesFileLocationConfigProperty());
        addToRegistry(new ProxyEnabledConfigProperty());
        addToRegistry(new ProxyUrlConfigProperty());
        addToRegistry(new SocketTimeoutConfigProperty());
        addToRegistry(new UseConfigFileConfigProperty());
        addToRegistry(new SourceAddressConfigProperty());
        addToRegistry(new ForceIpV4ConfigProperty());
        addToRegistry(new ForceIpV6ConfigProperty());
        addToRegistry(new AuthUsernameConfigProperty());
        addToRegistry(new AuthPasswordConfigProperty());
        addToRegistry(new TwoFactorCodeConfigProperty());
        addToRegistry(new NetrcConfigProperty());
        addToRegistry(new VideoPasswordConfigProperty());
        addToRegistry(new WriteSubtitlesConfigProperty());
        addToRegistry(new EmbedSubtitlesConfigProperty());
        addToRegistry(new PreferredSubtitlesConfigProperty());

        /* Misc */
        addToRegistry(new DownloaderConfigProperty());
        addToRegistry(new HistoryEntriesNumberConfigProperty());
        addToRegistry(new RecentDownloadPathConfigProperty());
        addToRegistry(new HistoryTableConfigProperty());

        /* UI */
        addToRegistry(new MainWindowPosXConfigProperty());
        addToRegistry(new MainWindowPosYConfigProperty());
        addToRegistry(new MainWindowWidthConfigProperty());
        addToRegistry(new MainWindowHeightConfigProperty());
    }

    @SuppressWarnings("unchecked")
    private static <T extends ConfigProperty<?, ?>> void addToRegistry(T pref) {
        REGISTRY.put((Class<? extends ConfigProperty<?, ?>>) pref.getClass(), pref);
    }

    @SuppressWarnings("unchecked")
    public <T extends ConfigProperty<?, ?>> T get(Class<T> clazz) {
        return (T) REGISTRY.get(clazz);
    }

    public void dropUnsavedChanges() {
        for (ConfigProperty<?, ?> value : REGISTRY.values()) {
            value.restore();
        }
    }

    public void saveAll() {
        for (ConfigProperty<?, ?> value : REGISTRY.values()) {
            value.save();
        }
    }
}
