package com.github.engatec.vdl.core.youtubedl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.github.engatec.vdl.TestHelper;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import com.github.engatec.vdl.model.preferences.wrapper.misc.DownloaderPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.AuthUsernamePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.CookiesFileLocationPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.EmbedSubtitlesPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV4Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ForceIpV6Pref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.MarkWatchedPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NetrcPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoContinuePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoPartPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.PreferredSubtitlesPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.RateLimitPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ReadCookiesPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SourceAddressPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.TwoFactorCodePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.VideoPasswordPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.WriteSubtitlesPref;
import com.github.engatec.vdl.model.preferences.youtubedl.RateLimitConfigItem;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

public class YoutubeDlCommandHelperTests {

    @BeforeAll
    static void setUp() {
        TestHelper.initTestApplicationContext();
        mockPreference(DownloaderPref.class, 1);
    }

    private static <V, T extends ConfigItemWrapper<?, V>> void mockPreference(Class<T> configItemClass, V value) {
        T prefMock = Mockito.mock(configItemClass);
        Mockito.when(ApplicationContext.getInstance().getConfigRegistry().get(configItemClass)).thenReturn(prefMock);
        Mockito.when(prefMock.getValue()).thenReturn(value);
    }

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
    class GeneralOptionsTests {

        @BeforeEach
        void setUp() {
            mockPreference(MarkWatchedPref.class, false);
            mockPreference(NoContinuePref.class, false);
            mockPreference(NoPartPref.class, false);
            mockPreference(NoMTimePref.class, false);
            mockPreference(ReadCookiesPref.class, false);
            mockPreference(CookiesFileLocationPref.class, StringUtils.EMPTY);
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
            mockPreference(MarkWatchedPref.class, true);
            doAssertions(buildCommand(), "--mark-watched");
        }

        @Test
        void shouldSetNoContinue() {
            mockPreference(NoContinuePref.class, true);
            doAssertions(buildCommand(), "--no-continue");
        }

        @Test
        void shouldSetNoPart() {
            mockPreference(NoPartPref.class, true);
            doAssertions(buildCommand(), "--no-part");
        }

        @Test
        void shouldSetNoMTime() {
            mockPreference(NoMTimePref.class, true);
            doAssertions(buildCommand(), "--no-mtime");
        }

        @Test
        void shouldSetCookies() throws Exception {
            Path tempFile = Files.createTempFile(null, null);
            mockPreference(ReadCookiesPref.class, true);
            mockPreference(CookiesFileLocationPref.class, tempFile.toString());
            doAssertions(buildCommand(), "--cookies", tempFile.toString());
            Files.deleteIfExists(tempFile);
        }

        @Test
        void shouldNotSetCookies_fileNotExists() {
            String path = "path";
            mockPreference(ReadCookiesPref.class, true);
            mockPreference(CookiesFileLocationPref.class, path);
            assertThat(buildCommand())
                    .hasSize(1)
                    .doesNotContain("--cookies");
        }

        @Test
        void shouldNoSetCookies_readCookiesPropertyIsFalse() {
            String path = "path";
            mockPreference(ReadCookiesPref.class, false);
            mockPreference(CookiesFileLocationPref.class, path);
            assertThat(buildCommand())
                    .hasSize(1)
                    .doesNotContain("--cookies");
        }
    }

    @Nested
    @DisplayName("Subtitles options")
    class SubtitlesOptionsTests {
        @BeforeEach
        void setUp() {
            mockPreference(WriteSubtitlesPref.class, false);
            mockPreference(EmbedSubtitlesPref.class, true);
            mockPreference(PreferredSubtitlesPref.class, StringUtils.EMPTY);
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setSubtitlesOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @Test
        void shouldNotHaveAnySubtitlesOption() {
            assertThat(buildCommand())
                    .hasSize(1)
                    .doesNotContainAnyElementsOf(List.of("--write-sub", "--sub-lang", "--embed-subs"));
        }

        @Test
        void shouldHaveNeitherEmbedSubsNorSubLang() {
            mockPreference(WriteSubtitlesPref.class, true);
            mockPreference(EmbedSubtitlesPref.class, false);
            assertThat(buildCommand())
                    .hasSize(5)
                    .contains("--write-sub", "--all-subs", "--convert-subs", "srt")
                    .doesNotContain("--sub-lang", "--embed-subs");
        }

        @Test
        void shouldHaveSubLang() {
            mockPreference(WriteSubtitlesPref.class, true);
            mockPreference(EmbedSubtitlesPref.class, false);
            mockPreference(PreferredSubtitlesPref.class, "en,ru");
            assertThat(buildCommand())
                    .hasSize(6)
                    .contains("--write-sub", "--sub-lang", "--convert-subs", "srt")
                    .containsAnyOf("en,ru", "ru,en");
        }

        @Test
        void shouldHaveEmbedSub() {
            mockPreference(WriteSubtitlesPref.class, true);
            mockPreference(EmbedSubtitlesPref.class, true);
            assertThat(buildCommand())
                    .hasSize(6)
                    .contains("--write-sub", "--all-subs", "--convert-subs", "srt", "--embed-subs")
                    .doesNotContain("--sub-lang");
        }
    }

    @Nested
    @DisplayName("Download options")
    class DownloadOptionsTests {

        @BeforeEach
        void setUp() {
            mockPreference(RateLimitPref.class, RateLimitConfigItem.DEFAULT);
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setDownloadOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @ParameterizedTest
        @ValueSource(strings = {"50", "4.2M"})
        void shouldSetRateLimit(String limit) {
            mockPreference(RateLimitPref.class, limit);
            List<String> command = buildCommand();
            assertThat(command).hasSize(3).contains("-r", limit);
        }

        @ParameterizedTest
        @ValueSource(strings = {StringUtils.EMPTY, RateLimitConfigItem.DEFAULT})
        void shouldNotSetRateLimit(String limit) {
            mockPreference(RateLimitPref.class, limit);
            List<String> command = buildCommand();
            assertThat(command).hasSize(1).doesNotContain("-r");
        }
    }

    @Nested
    @DisplayName("Network options")
    class NetworkOptionsTests {

        @BeforeEach
        void setUp() {
            mockPreference(ProxyUrlPref.class, StringUtils.EMPTY);
            mockPreference(SocketTimeoutPref.class, StringUtils.EMPTY);
            mockPreference(SourceAddressPref.class, StringUtils.EMPTY);
            mockPreference(ForceIpV4Pref.class, false);
            mockPreference(ForceIpV6Pref.class, false);
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
            mockPreference(ProxyUrlPref.class, proxyUrl);
            doAssertions(buildCommand(), "--proxy", proxyUrl);
        }

        @Test
        void shouldSetSocketTimeout() {
            String timeout = "10";
            mockPreference(SocketTimeoutPref.class, timeout);
            doAssertions(buildCommand(), "--socket-timeout", timeout);
        }

        @Test
        void shouldSetSourceAddress() {
            String ip = "127.0.0.1";
            mockPreference(SourceAddressPref.class, ip);
            doAssertions(buildCommand(), "--source-address", ip);
        }

        @Test
        void shouldSetForceIpV4() {
            mockPreference(ForceIpV4Pref.class, true);
            doAssertions(buildCommand(), "--force-ipv4");
        }

        @Test
        void shouldSetForceIpV6() {
            mockPreference(ForceIpV6Pref.class, true);
            doAssertions(buildCommand(), "--force-ipv6");
        }

        @Test
        void shouldSetMultiple() {
            mockPreference(ProxyUrlPref.class, "http://proxy");
            mockPreference(SocketTimeoutPref.class, "10");
            mockPreference(SourceAddressPref.class, "127.0.0.1");
            mockPreference(ForceIpV4Pref.class, true);
            List<String> command = buildCommand();
            assertThat(command)
                    .hasSize(8)
                    .contains("--proxy", "--socket-timeout", "--source-address", "--force-ipv4")
                    .doesNotContain("--force-ipv6");
        }
    }

    @Nested
    @DisplayName("Authenticated options")
    class AuthenticationOptionsTests {

        @BeforeEach
        void setUp() {
            mockPreference(AuthUsernamePref.class, StringUtils.EMPTY);
            mockPreference(AuthPasswordPref.class, StringUtils.EMPTY);
            mockPreference(TwoFactorCodePref.class, StringUtils.EMPTY);
            mockPreference(NetrcPref.class, false);
            mockPreference(VideoPasswordPref.class, StringUtils.EMPTY);
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
            mockPreference(AuthUsernamePref.class, username);
            doAssertions(buildCommand(), "-u", username);
        }

        @Test
        void shouldSetPassword() {
            String password = "pass";
            mockPreference(AuthPasswordPref.class, password);
            doAssertions(buildCommand(), "-p", password);
        }

        @Test
        void shouldNotSetTwoFactorCode_noUsernameOrPassword() {
            String code = "pass";
            mockPreference(TwoFactorCodePref.class, code);
            assertThat(buildCommand()).hasSize(1).doesNotContain("-2");
        }

        @Test
        void shouldSetTwoFactorCode() {
            String code = "pass";
            mockPreference(AuthUsernamePref.class, "usr");
            mockPreference(AuthPasswordPref.class, "pass");
            mockPreference(TwoFactorCodePref.class, code);
            assertThat(buildCommand()).hasSize(7)
                    .contains("-2", "-u", "-p");
        }

        @Test
        void shouldSetNetrc() {
            mockPreference(NetrcPref.class, true);
            doAssertions(buildCommand(), "--netrc");
        }

        @Test
        void shouldSetVideoPassword() {
            String code = "pass";
            mockPreference(VideoPasswordPref.class, code);
            doAssertions(buildCommand(), "--video-password", code);
        }

        @Test
        void shouldSetMultiple() {
            mockPreference(AuthUsernamePref.class, "usr");
            mockPreference(AuthPasswordPref.class, "pass");
            mockPreference(TwoFactorCodePref.class, "pass");
            mockPreference(VideoPasswordPref.class, "pass");
            List<String> command = buildCommand();
            assertThat(command)
                    .hasSize(9)
                    .contains("-u", "-p", "-2", "--video-password")
                    .doesNotContain("--netrc");
        }
    }
}
