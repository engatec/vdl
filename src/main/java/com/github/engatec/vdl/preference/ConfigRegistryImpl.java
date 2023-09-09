package com.github.engatec.vdl.preference;

import java.util.HashMap;
import java.util.Map;

import com.github.engatec.vdl.preference.property.ConfigProperty;
import com.github.engatec.vdl.preference.property.engine.AuthPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.engine.AuthUsernameConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ConfigFilePathConfigProperty;
import com.github.engatec.vdl.preference.property.engine.CookiesFileLocationConfigProperty;
import com.github.engatec.vdl.preference.property.engine.EmbedSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ForceIpV4ConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ForceIpV6ConfigProperty;
import com.github.engatec.vdl.preference.property.engine.MarkWatchedConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NetrcConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NoContinueConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NoMTimeConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NoPartConfigProperty;
import com.github.engatec.vdl.preference.property.engine.OutputTemplateConfigProperty;
import com.github.engatec.vdl.preference.property.engine.PreferredSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ProxyEnabledConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ProxyUrlConfigProperty;
import com.github.engatec.vdl.preference.property.engine.RateLimitConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ReadCookiesConfigProperty;
import com.github.engatec.vdl.preference.property.engine.SocketTimeoutConfigProperty;
import com.github.engatec.vdl.preference.property.engine.SourceAddressConfigProperty;
import com.github.engatec.vdl.preference.property.engine.TwoFactorCodeConfigProperty;
import com.github.engatec.vdl.preference.property.engine.UseConfigFileConfigProperty;
import com.github.engatec.vdl.preference.property.engine.VideoPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.engine.WriteSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.general.AlwaysAskDownloadPathConfigProperty;
import com.github.engatec.vdl.preference.property.general.AudioExtractionAddMetadataConfigProperty;
import com.github.engatec.vdl.preference.property.general.AudioExtractionBitrateConfigProperty;
import com.github.engatec.vdl.preference.property.general.AudioExtractionBitrateTypeConfigProperty;
import com.github.engatec.vdl.preference.property.general.AudioExtractionEmbedThumbnailConfigProperty;
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
import com.github.engatec.vdl.preference.property.misc.MultiSearchConfigProperty;
import com.github.engatec.vdl.preference.property.misc.RecentDownloadPathConfigProperty;
import com.github.engatec.vdl.preference.property.table.DownloadsTableConfigProperty;
import com.github.engatec.vdl.preference.property.table.HistoryTableConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowHeightConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowPosXConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowPosYConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowWidthConfigProperty;

public class ConfigRegistryImpl implements ConfigRegistry {

    private static final Map<Class<? extends ConfigProperty<?, ?>>, ConfigProperty<?, ?>> REGISTRY = new HashMap<>();

    static {
        /* General */
        addToRegistry(new AlwaysAskDownloadPathConfigProperty());
        addToRegistry(new AudioExtractionAddMetadataConfigProperty());
        addToRegistry(new AudioExtractionBitrateConfigProperty());
        addToRegistry(new AudioExtractionBitrateTypeConfigProperty());
        addToRegistry(new AudioExtractionEmbedThumbnailConfigProperty());
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
        addToRegistry(new MultiSearchConfigProperty());
        addToRegistry(new RecentDownloadPathConfigProperty());

        /* UI */
        addToRegistry(new MainWindowPosXConfigProperty());
        addToRegistry(new MainWindowPosYConfigProperty());
        addToRegistry(new MainWindowWidthConfigProperty());
        addToRegistry(new MainWindowHeightConfigProperty());

        /* Table */
        addToRegistry(new DownloadsTableConfigProperty());
        addToRegistry(new HistoryTableConfigProperty());
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
