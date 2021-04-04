package com.github.engatec.vdl.core.youtubedl;

import java.util.List;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.CookiesFileLocationPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.MarkWatchedPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NetrcPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoContinuePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoPartPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ReadCookiesPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.TwoFactorCodePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.VideoPasswordPref;
import org.apache.commons.lang3.StringUtils;
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
    @DisplayName("General options")
    class GeneralOptions {

        @BeforeEach
        void setUp() {
            ConfigRegistry.get(MarkWatchedPref.class).setValue(false);
            ConfigRegistry.get(NoContinuePref.class).setValue(false);
            ConfigRegistry.get(NoPartPref.class).setValue(false);
            ConfigRegistry.get(NoMTimePref.class).setValue(false);
            ConfigRegistry.get(ReadCookiesPref.class).setValue(false);
            ConfigRegistry.get(CookiesFileLocationPref.class).setValue(StringUtils.EMPTY);
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setGeneralOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @Test
        void shouldHaveNoSettingsByDefault() {
            List<String> command = buildCommand();
            assertThat(command).hasSize(1);
        }

        @Test
        void shouldSetMarkWatched() {
            ConfigRegistry.get(MarkWatchedPref.class).getProperty().setValue(true);
            doAssertions(buildCommand(), "--mark-watched");
        }

        @Test
        void shouldSetNoContinue() {
            ConfigRegistry.get(NoContinuePref.class).getProperty().setValue(true);
            doAssertions(buildCommand(), "--no-continue");
        }

        @Test
        void shouldSetNoPart() {
            ConfigRegistry.get(NoPartPref.class).getProperty().setValue(true);
            doAssertions(buildCommand(), "--no-part");
        }

        @Test
        void shouldSetNoMTime() {
            ConfigRegistry.get(NoMTimePref.class).getProperty().setValue(true);
            doAssertions(buildCommand(), "--no-mtime");
        }

        @Test
        void shouldSetCookies() {
            String path = "path";
            ConfigRegistry.get(ReadCookiesPref.class).getProperty().setValue(true);
            ConfigRegistry.get(CookiesFileLocationPref.class).getProperty().setValue(path);
            doAssertions(buildCommand(), "--cookies", path);
        }

        @Test
        void shouldNoSetCookies() {
            String path = "path";
            ConfigRegistry.get(CookiesFileLocationPref.class).getProperty().setValue(path);
            List<String> command = buildCommand();
            assertThat(command)
                    .hasSize(1)
                    .doesNotContain("--cookies");
        }
    }

    @Nested
    @DisplayName("Network options")
    class NetworkOptions {

        @BeforeEach
        void setUp() {
            ConfigRegistry.get(ProxyUrlPref.class).setValue(StringUtils.EMPTY);
            ConfigRegistry.get(SocketTimeoutPref.class).setValue(StringUtils.EMPTY);
            ConfigRegistry.get(SourceAddressPref.class).setValue(StringUtils.EMPTY);
            ConfigRegistry.get(ForceIpV4Pref.class).setValue(false);
            ConfigRegistry.get(ForceIpV6Pref.class).setValue(false);
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setNetworkOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @Test
        void shouldHaveNoSettingsByDefault() {
            List<String> command = buildCommand();
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
            ConfigRegistry.get(AuthUsernamePref.class).setValue(StringUtils.EMPTY);
            ConfigRegistry.get(AuthPasswordPref.class).setValue(StringUtils.EMPTY);
            ConfigRegistry.get(TwoFactorCodePref.class).setValue(StringUtils.EMPTY);
            ConfigRegistry.get(NetrcPref.class).setValue(false);
            ConfigRegistry.get(VideoPasswordPref.class).setValue(StringUtils.EMPTY);
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setAuthenticationOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @Test
        void shouldHaveNoSettingsByDefault() {
            List<String> command = buildCommand();
            assertThat(command).hasSize(1);
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

        @Test
        void shouldNotSetTwoFactorCode_noUsernameOrPassword() {
            String code = "pass";
            ConfigRegistry.get(TwoFactorCodePref.class).getProperty().setValue(code);
            assertThat(buildCommand()).hasSize(1).doesNotContain("-2");
        }

        @Test
        void shouldSetTwoFactorCode() {
            String code = "pass";
            ConfigRegistry.get(AuthUsernamePref.class).getProperty().setValue("usr");
            ConfigRegistry.get(AuthPasswordPref.class).getProperty().setValue("pass");
            ConfigRegistry.get(TwoFactorCodePref.class).getProperty().setValue(code);
            assertThat(buildCommand()).hasSize(7)
                    .contains("-2", "-u", "-p");
        }

        @Test
        void shouldSetNetrc() {
            ConfigRegistry.get(NetrcPref.class).getProperty().setValue(true);
            doAssertions(buildCommand(), "--netrc");
        }

        @Test
        void shouldSetVideoPassword() {
            String code = "pass";
            ConfigRegistry.get(VideoPasswordPref.class).getProperty().setValue(code);
            doAssertions(buildCommand(), "--video-password", code);
        }

        @Test
        void shouldSetMultiple() {
            ConfigRegistry.get(AuthUsernamePref.class).getProperty().setValue("usr");
            ConfigRegistry.get(AuthPasswordPref.class).getProperty().setValue("pass");
            ConfigRegistry.get(TwoFactorCodePref.class).getProperty().setValue("pass");
            ConfigRegistry.get(VideoPasswordPref.class).getProperty().setValue("pass");
            List<String> command = buildCommand();
            assertThat(command)
                    .hasSize(9)
                    .contains("-u", "-p", "-2", "--video-password")
                    .doesNotContain("--netrc");
        }
    }
}
