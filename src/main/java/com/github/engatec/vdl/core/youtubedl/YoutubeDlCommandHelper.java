package com.github.engatec.vdl.core.youtubedl;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
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
    }
}
