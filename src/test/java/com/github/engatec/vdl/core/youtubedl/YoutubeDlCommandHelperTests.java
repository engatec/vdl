package com.github.engatec.vdl.core.youtubedl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.github.engatec.vdl.TestHelper;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.preference.configitem.youtubedl.RateLimitConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import com.github.engatec.vdl.preference.property.engine.AuthPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.engine.AuthUsernameConfigProperty;
import com.github.engatec.vdl.preference.property.engine.CookiesFileLocationConfigProperty;
import com.github.engatec.vdl.preference.property.engine.EmbedSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ForceIpV4ConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ForceIpV6ConfigProperty;
import com.github.engatec.vdl.preference.property.engine.MarkWatchedConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NetrcConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NoContinueConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NoMTimeConfigProperty;
import com.github.engatec.vdl.preference.property.engine.NoPartConfigProperty;
import com.github.engatec.vdl.preference.property.engine.PreferredSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ProxyEnabledConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ProxyUrlConfigProperty;
import com.github.engatec.vdl.preference.property.engine.RateLimitConfigProperty;
import com.github.engatec.vdl.preference.property.engine.ReadCookiesConfigProperty;
import com.github.engatec.vdl.preference.property.engine.SocketTimeoutConfigProperty;
import com.github.engatec.vdl.preference.property.engine.SourceAddressConfigProperty;
import com.github.engatec.vdl.preference.property.engine.TwoFactorCodeConfigProperty;
import com.github.engatec.vdl.preference.property.engine.VideoPasswordConfigProperty;
import com.github.engatec.vdl.preference.property.engine.WriteSubtitlesConfigProperty;
import com.github.engatec.vdl.preference.property.misc.DownloaderConfigProperty;
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
        mockPreference(DownloaderConfigProperty.class, 1);
    }

    private static <V, T extends ConfigProperty<?, V>> void mockPreference(Class<T> configItemClass, V value) {
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
            mockPreference(MarkWatchedConfigProperty.class, false);
            mockPreference(NoContinueConfigProperty.class, false);
            mockPreference(NoPartConfigProperty.class, false);
            mockPreference(NoMTimeConfigProperty.class, false);
            mockPreference(ReadCookiesConfigProperty.class, false);
            mockPreference(CookiesFileLocationConfigProperty.class, StringUtils.EMPTY);
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
            mockPreference(MarkWatchedConfigProperty.class, true);
            doAssertions(buildCommand(), "--mark-watched");
        }

        @Test
        void shouldSetNoContinue() {
            mockPreference(NoContinueConfigProperty.class, true);
            doAssertions(buildCommand(), "--no-continue");
        }

        @Test
        void shouldSetNoPart() {
            mockPreference(NoPartConfigProperty.class, true);
            doAssertions(buildCommand(), "--no-part");
        }

        @Test
        void shouldSetNoMTime() {
            mockPreference(NoMTimeConfigProperty.class, true);
            doAssertions(buildCommand(), "--no-mtime");
        }

        @Test
        void shouldSetCookies() throws Exception {
            Path tempFile = Files.createTempFile(null, null);
            mockPreference(ReadCookiesConfigProperty.class, true);
            mockPreference(CookiesFileLocationConfigProperty.class, tempFile.toString());
            doAssertions(buildCommand(), "--cookies", tempFile.toString());
            Files.deleteIfExists(tempFile);
        }

        @Test
        void shouldNotSetCookies_fileNotExists() {
            String path = "path";
            mockPreference(ReadCookiesConfigProperty.class, true);
            mockPreference(CookiesFileLocationConfigProperty.class, path);
            assertThat(buildCommand())
                    .hasSize(1)
                    .doesNotContain("--cookies");
        }

        @Test
        void shouldNoSetCookies_readCookiesPropertyIsFalse() {
            String path = "path";
            mockPreference(ReadCookiesConfigProperty.class, false);
            mockPreference(CookiesFileLocationConfigProperty.class, path);
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
            mockPreference(WriteSubtitlesConfigProperty.class, false);
            mockPreference(EmbedSubtitlesConfigProperty.class, true);
            mockPreference(PreferredSubtitlesConfigProperty.class, StringUtils.EMPTY);
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
            mockPreference(WriteSubtitlesConfigProperty.class, true);
            mockPreference(EmbedSubtitlesConfigProperty.class, false);
            assertThat(buildCommand())
                    .hasSize(5)
                    .contains("--write-sub", "--all-subs", "--convert-subs", "srt")
                    .doesNotContain("--sub-lang", "--embed-subs");
        }

        @Test
        void shouldHaveSubLang() {
            mockPreference(WriteSubtitlesConfigProperty.class, true);
            mockPreference(EmbedSubtitlesConfigProperty.class, false);
            mockPreference(PreferredSubtitlesConfigProperty.class, "en,ru");
            assertThat(buildCommand())
                    .hasSize(6)
                    .contains("--write-sub", "--sub-lang", "--convert-subs", "srt")
                    .containsAnyOf("en,ru", "ru,en");
        }

        @Test
        void shouldHaveEmbedSub() {
            mockPreference(WriteSubtitlesConfigProperty.class, true);
            mockPreference(EmbedSubtitlesConfigProperty.class, true);
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
            mockPreference(RateLimitConfigProperty.class, RateLimitConfigItem.DEFAULT);
        }

        private List<String> buildCommand() {
            YoutubeDlCommandBuilder commandBuilder = YoutubeDlCommandBuilder.newInstance();
            YoutubeDlCommandHelper.setDownloadOptions(commandBuilder);
            return commandBuilder.buildAsList();
        }

        @ParameterizedTest
        @ValueSource(strings = {"50", "4.2M"})
        void shouldSetRateLimit(String limit) {
            mockPreference(RateLimitConfigProperty.class, limit);
            List<String> command = buildCommand();
            assertThat(command).hasSize(3).contains("-r", limit);
        }

        @ParameterizedTest
        @ValueSource(strings = {StringUtils.EMPTY, RateLimitConfigItem.DEFAULT})
        void shouldNotSetRateLimit(String limit) {
            mockPreference(RateLimitConfigProperty.class, limit);
            List<String> command = buildCommand();
            assertThat(command).hasSize(1).doesNotContain("-r");
        }
    }

    @Nested
    @DisplayName("Network options")
    class NetworkOptionsTests {

        @BeforeEach
        void setUp() {
            mockPreference(ProxyUrlConfigProperty.class, StringUtils.EMPTY);
            mockPreference(SocketTimeoutConfigProperty.class, StringUtils.EMPTY);
            mockPreference(SourceAddressConfigProperty.class, StringUtils.EMPTY);
            mockPreference(ForceIpV4ConfigProperty.class, false);
            mockPreference(ForceIpV6ConfigProperty.class, false);
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
        void shouldSetProxy() {
            String proxyUrl = "http://proxy";
            mockPreference(ProxyEnabledConfigProperty.class, true);
            mockPreference(ProxyUrlConfigProperty.class, proxyUrl);
            doAssertions(buildCommand(), "--proxy", proxyUrl);
        }

        @Test
        void shouldNotSetProxy_proxyDisabled() {
            mockPreference(ProxyEnabledConfigProperty.class, false);
            mockPreference(ProxyUrlConfigProperty.class, "http://proxy");
            List<String> command = buildCommand();
            assertThat(command).hasSize(1);
        }

        @Test
        void shouldNotSetProxy_proxyUrlEmpty() {
            mockPreference(ProxyEnabledConfigProperty.class, true);
            List<String> command = buildCommand();
            assertThat(command).hasSize(1);
        }

        @Test
        void shouldSetSocketTimeout() {
            String timeout = "10";
            mockPreference(SocketTimeoutConfigProperty.class, timeout);
            doAssertions(buildCommand(), "--socket-timeout", timeout);
        }

        @Test
        void shouldSetSourceAddress() {
            String ip = "127.0.0.1";
            mockPreference(SourceAddressConfigProperty.class, ip);
            doAssertions(buildCommand(), "--source-address", ip);
        }

        @Test
        void shouldSetForceIpV4() {
            mockPreference(ForceIpV4ConfigProperty.class, true);
            doAssertions(buildCommand(), "--force-ipv4");
        }

        @Test
        void shouldSetForceIpV6() {
            mockPreference(ForceIpV6ConfigProperty.class, true);
            doAssertions(buildCommand(), "--force-ipv6");
        }

        @Test
        void shouldSetMultiple() {
            mockPreference(ProxyEnabledConfigProperty.class, true);
            mockPreference(ProxyUrlConfigProperty.class, "http://proxy");
            mockPreference(SocketTimeoutConfigProperty.class, "10");
            mockPreference(SourceAddressConfigProperty.class, "127.0.0.1");
            mockPreference(ForceIpV4ConfigProperty.class, true);
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
            mockPreference(AuthUsernameConfigProperty.class, StringUtils.EMPTY);
            mockPreference(AuthPasswordConfigProperty.class, StringUtils.EMPTY);
            mockPreference(TwoFactorCodeConfigProperty.class, StringUtils.EMPTY);
            mockPreference(NetrcConfigProperty.class, false);
            mockPreference(VideoPasswordConfigProperty.class, StringUtils.EMPTY);
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
            mockPreference(AuthUsernameConfigProperty.class, username);
            doAssertions(buildCommand(), "-u", username);
        }

        @Test
        void shouldSetPassword() {
            String password = "pass";
            mockPreference(AuthPasswordConfigProperty.class, password);
            doAssertions(buildCommand(), "-p", password);
        }

        @Test
        void shouldNotSetTwoFactorCode_noUsernameOrPassword() {
            String code = "pass";
            mockPreference(TwoFactorCodeConfigProperty.class, code);
            assertThat(buildCommand()).hasSize(1).doesNotContain("-2");
        }

        @Test
        void shouldSetTwoFactorCode() {
            String code = "pass";
            mockPreference(AuthUsernameConfigProperty.class, "usr");
            mockPreference(AuthPasswordConfigProperty.class, "pass");
            mockPreference(TwoFactorCodeConfigProperty.class, code);
            assertThat(buildCommand()).hasSize(7)
                    .contains("-2", "-u", "-p");
        }

        @Test
        void shouldSetNetrc() {
            mockPreference(NetrcConfigProperty.class, true);
            doAssertions(buildCommand(), "--netrc");
        }

        @Test
        void shouldSetVideoPassword() {
            String code = "pass";
            mockPreference(VideoPasswordConfigProperty.class, code);
            doAssertions(buildCommand(), "--video-password", code);
        }

        @Test
        void shouldSetMultiple() {
            mockPreference(AuthUsernameConfigProperty.class, "usr");
            mockPreference(AuthPasswordConfigProperty.class, "pass");
            mockPreference(TwoFactorCodeConfigProperty.class, "pass");
            mockPreference(VideoPasswordConfigProperty.class, "pass");
            List<String> command = buildCommand();
            assertThat(command)
                    .hasSize(9)
                    .contains("-u", "-p", "-2", "--video-password")
                    .doesNotContain("--netrc");
        }
    }
}
