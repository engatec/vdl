package com.github.engatec.vdl.core.youtubedl;

import java.util.List;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

public class YoutubeDlCommandHelperTests {

    private void doAssertions(List<String> command, String key) {
        assertThat(command).hasSize(2);
        assertThat(command).contains(key, atIndex(1));
    }

    private void doAssertions(List<String> command, String key, String value) {
        assertThat(command).hasSize(3);
        assertThat(command).contains(key, atIndex(1));
        assertThat(command).contains(value, atIndex(2));
    }

    @Nested
    @DisplayName("Network options")
    class NetworkOptions {

        @BeforeEach
        void setUp() {
            ConfigRegistry.get(ProxyUrlPref.class).restore();
            ConfigRegistry.get(SocketTimeoutPref.class).restore();
            ConfigRegistry.get(SourceAddressPref.class).restore();
            ConfigRegistry.get(ForceIpV4Pref.class).restore();
            ConfigRegistry.get(ForceIpV6Pref.class).restore();
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @Test
        void shouldHaveNoSettingsByDefault() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
            List<String> command = commandBuilder.buildAsList();
            assertThat(command).hasSize(1);
        }

        @Test
        void shouldSetProxyUrl() {
            String proxyUrl = "http://proxy";
            ConfigRegistry.get(ProxyUrlPref.class).getProperty().setValue(proxyUrl);
            doAssertions(buildCommand(), "--proxy", proxyUrl);
        }

        @Test
        void shouldSetSocketTimeout() {
            String timeout = "10";
            ConfigRegistry.get(SocketTimeoutPref.class).getProperty().setValue(timeout);
            doAssertions(buildCommand(), "--socket-timeout", timeout);
        }

        @Test
        void shouldSetSourceAddress() {
            String ip = "127.0.0.1";
            ConfigRegistry.get(SourceAddressPref.class).getProperty().setValue(ip);
            doAssertions(buildCommand(), "--source-address", ip);
        }

        @Test
        void shouldSetForceIpV4() {
            ConfigRegistry.get(ForceIpV4Pref.class).getProperty().setValue(true);
            doAssertions(buildCommand(), "--force-ipv4");
        }

        @Test
        void shouldSetForceIpV6() {
            ConfigRegistry.get(ForceIpV6Pref.class).getProperty().setValue(true);
            doAssertions(buildCommand(), "--force-ipv6");
        }

        @Test
        void shouldSetMultiple() {
            ConfigRegistry.get(ProxyUrlPref.class).getProperty().setValue("http://proxy");
            ConfigRegistry.get(SocketTimeoutPref.class).getProperty().setValue("10");
            ConfigRegistry.get(SourceAddressPref.class).getProperty().setValue("127.0.0.1");
            ConfigRegistry.get(ForceIpV4Pref.class).getProperty().setValue(true);
            List<String> command = buildCommand();
            assertThat(command)
                    .hasSize(8)
                    .contains("--proxy", "--socket-timeout", "--source-address", "--force-ipv4")
                    .doesNotContain("--force-ipv6");
        }
    }

    @Nested
    @DisplayName("Authenticated options")
    class AuthenticationOptions {

        @BeforeEach
        void setUp() {
            ConfigRegistry.get(AuthUsernamePref.class).restore();
            ConfigRegistry.get(AuthPasswordPref.class).restore();
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setAuthenticationOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @Test
        void shouldSetUsername() {
            String username = "usr";
            ConfigRegistry.get(AuthUsernamePref.class).getProperty().setValue(username);
            doAssertions(buildCommand(), "-u", username);
        }

        @Test
        void shouldSetPassword() {
            String password = "pass";
            ConfigRegistry.get(AuthPasswordPref.class).getProperty().setValue(password);
            doAssertions(buildCommand(), "-p", password);
        }
    }
}
