package com.github.engatec.vdl.core.youtubedl;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YoutubeDlCommandHelper {

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDlCommandHelper.class);

    public static void setNetworkOptions(YoutubeDlCommandBuilder commandBuilder) {
        String proxyUrl = StringUtils.strip(ConfigRegistry.get(ProxyUrlPref.class).getValue());
        if (StringUtils.isNotBlank(proxyUrl)) {
            commandBuilder.proxy(proxyUrl);
        }

        String socketTimeout = StringUtils.strip(ConfigRegistry.get(SocketTimeoutPref.class).getValue());
        if (StringUtils.isNotBlank(socketTimeout)) {
            try {
                int timeout = Integer.parseInt(socketTimeout);
                commandBuilder.socketTimeout(timeout);
            } catch (NumberFormatException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        String sourceAddress = StringUtils.strip(ConfigRegistry.get(SourceAddressPref.class).getValue());
        if (StringUtils.isNotBlank(sourceAddress)) {
            commandBuilder.sourceAddress(sourceAddress);
        }

        Boolean forceIpV4 = ConfigRegistry.get(ForceIpV4Pref.class).getValue();
        if (forceIpV4) {
            commandBuilder.forceIpV4();
        }

        Boolean forceIpV6 = ConfigRegistry.get(ForceIpV6Pref.class).getValue();
        if (forceIpV6) {
            commandBuilder.forceIpV6();
        }
    }
}
