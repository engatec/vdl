package com.github.engatec.vdl.core.youtubedl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.preference.ConfigRegistry;
import com.github.engatec.vdl.preference.configitem.youtubedl.RateLimitConfigItem;
import com.github.engatec.vdl.preference.property.youtubedl.AuthPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.AuthUsernameConfigProperty;
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
import com.github.engatec.vdl.preference.property.youtubedl.VideoPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.youtubedl.WriteSubtitlesConfigProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.strip;

public class YoutubeDlCommandHelper {

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDlCommandHelper.class);

    private static final ConfigRegistry configRegistry = ApplicationContext.getInstance().getConfigRegistry();

    public static void setOutputPath(YoutubeDlCommandBuilder commandBuilder, Downloadable downloadable) {
        String outputTemplate = configRegistry.get(OutputTemplateConfigProperty.class).getValue();
        commandBuilder.outputPath(downloadable.getDownloadPath().resolve(outputTemplate).toString());
    }

    public static void setGeneralOptions(YoutubeDlCommandBuilder commandBuilder) {
        Boolean markWatched = configRegistry.get(MarkWatchedConfigProperty.class).getValue();
        if (markWatched) {
            commandBuilder.markWatched();
        }

        Boolean noContinue = configRegistry.get(NoContinueConfigProperty.class).getValue();
        if (noContinue) {
            commandBuilder.noContinue();
        }

        Boolean noPart = configRegistry.get(NoPartConfigProperty.class).getValue();
        if (noPart) {
            commandBuilder.noPart();
        }

        Boolean noMTime = configRegistry.get(NoMTimeConfigProperty.class).getValue();
        if (noMTime) {
            commandBuilder.noMTime();
        }

        Boolean readCookies = configRegistry.get(ReadCookiesConfigProperty.class).getValue();
        if (readCookies) {
            String cookiesFileLocation = configRegistry.get(CookiesFileLocationConfigProperty.class).getValue();
            Path cookiesPath = Path.of(cookiesFileLocation);
            if (Files.exists(cookiesPath) && Files.isReadable(cookiesPath)) {
                commandBuilder.cookiesFile(Path.of(cookiesFileLocation));
            }
        }
    }

    public static void setSubtitlesOptions(YoutubeDlCommandBuilder commandBuilder) {
        Boolean writeSubtitles = configRegistry.get(WriteSubtitlesConfigProperty.class).getValue();
        if (!writeSubtitles) {
            return;
        }

        String preferredSubtitlesConfigValue = configRegistry.get(PreferredSubtitlesConfigProperty.class).getValue();
        Set<String> preferredSubtitles = Arrays.stream(preferredSubtitlesConfigValue.split(","))
                .filter(StringUtils::isNotBlank)
                .map(StringUtils::strip)
                .collect(Collectors.toSet());
        commandBuilder.writeSub(preferredSubtitles);
        commandBuilder.convertSub("srt");

        Boolean embedSubtitles = configRegistry.get(EmbedSubtitlesConfigProperty.class).getValue();
        if (embedSubtitles) {
            commandBuilder.embedSub();
        }
    }

    public static void setDownloadOptions(YoutubeDlCommandBuilder commandBuilder) {
        String limit = StringUtils.defaultIfBlank(configRegistry.get(RateLimitConfigProperty.class).getValue(), RateLimitConfigItem.DEFAULT);
        if (!limit.equals(RateLimitConfigItem.DEFAULT)) {
            commandBuilder.rateLimit(limit);
        }
    }

    public static void setNetworkOptions(YoutubeDlCommandBuilder commandBuilder) {
        Boolean proxyEnabled = configRegistry.get(ProxyEnabledConfigProperty.class).getValue();
        String proxyUrl = strip(configRegistry.get(ProxyUrlConfigProperty.class).getValue());
        if (proxyEnabled && isNotBlank(proxyUrl)) {
            commandBuilder.proxy(proxyUrl);
        }

        String socketTimeout = strip(configRegistry.get(SocketTimeoutConfigProperty.class).getValue());
        if (isNotBlank(socketTimeout)) {
            try {
                int timeout = Integer.parseInt(socketTimeout);
                commandBuilder.socketTimeout(timeout);
            } catch (NumberFormatException e) {
                LOGGER.warn(e.getMessage());
            }
        }

        String sourceAddress = strip(configRegistry.get(SourceAddressConfigProperty.class).getValue());
        if (isNotBlank(sourceAddress)) {
            commandBuilder.sourceAddress(sourceAddress);
        }

        Boolean forceIpV4 = configRegistry.get(ForceIpV4ConfigProperty.class).getValue();
        if (forceIpV4) {
            commandBuilder.forceIpV4();
        }

        Boolean forceIpV6 = configRegistry.get(ForceIpV6ConfigProperty.class).getValue();
        if (forceIpV6) {
            commandBuilder.forceIpV6();
        }

        if (forceIpV4 && forceIpV6) {
            LOGGER.warn("Both forceIpV4 and forceIpV6 settings enabled!");
        }
    }

    public static void setAuthenticationOptions(YoutubeDlCommandBuilder commandBuilder) {
        String username = strip(configRegistry.get(AuthUsernameConfigProperty.class).getValue());
        if (isNotBlank(username)) {
            commandBuilder.username(username);
        }

        String password = strip(configRegistry.get(AuthPasswordConfigProperty.class).getValue());
        if (isNotBlank(password)) {
            commandBuilder.password(password);
        }

        if (isNotBlank(username) && isNotBlank(password)) {
            String twoFactorCode = configRegistry.get(TwoFactorCodeConfigProperty.class).getValue();
            if (isNotBlank(twoFactorCode)) {
                commandBuilder.twoFactor(twoFactorCode);
            }
        }

        Boolean netrc = configRegistry.get(NetrcConfigProperty.class).getValue();
        if (netrc) {
            commandBuilder.useNetrc();
        }

        String videoPassword = configRegistry.get(VideoPasswordConfigProperty.class).getValue();
        if (isNotBlank(videoPassword)) {
            commandBuilder.videoPassword(videoPassword);
        }
    }
}
