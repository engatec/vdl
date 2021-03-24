package com.github.engatec.vdl.core.youtubedl;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.strip;

public class YoutubeDlCommandHelper {

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDlCommandHelper.class);

    public static void setNetworkOptions(YoutubeDlCommandBuilder commandBuilder) {
        String proxyUrl = strip(ConfigRegistry.get(ProxyUrlPref.class).getValue());
        if (isNotBlank(proxyUrl)) {
            commandBuilder.proxy(proxyUrl);
        }

        String socketTimeout = strip(ConfigRegistry.get(SocketTimeoutPref.class).getValue());
        if (isNotBlank(socketTimeout)) {
            try {
                int timeout = Integer.parseInt(socketTimeout);
                commandBuilder.socketTimeout(timeout);
            } catch (NumberFormatException e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }

        String sourceAddress = strip(ConfigRegistry.get(SourceAddressPref.class).getValue());
        if (isNotBlank(sourceAddress)) {
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

        if (forceIpV4 && forceIpV6) {
            LOGGER.warn("Both forceIpV4 and forceIpV6 settings enabled!");
        }
    }

    public static void setAuthenticationOptions(YoutubeDlCommandBuilder commandBuilder) {
        String username = strip(ConfigRegistry.get(AuthUsernamePref.class).getValue());
        if (isNotBlank(username)) {
            commandBuilder.username(username);
        }

        String password = strip(ConfigRegistry.get(AuthPasswordPref.class).getValue());
        if (isNotBlank(password)) {
            commandBuilder.password(password);
        }
    }
}
