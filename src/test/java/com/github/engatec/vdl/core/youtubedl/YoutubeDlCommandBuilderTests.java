package com.github.engatec.vdl.core.youtubedl;

import java.nio.file.Path;
import java.util.List;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import com.github.engatec.vdl.model.preferences.wrapper.misc.DownloaderPref;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

public class YoutubeDlCommandBuilderTests {

    @BeforeAll
    static void setUp() {
        ConfigRegistry configRegistryMock = Mockito.mock(ConfigRegistry.class);
        ApplicationContext.INSTANCE.setConfigRegistry(configRegistryMock);

        mockPreference(DownloaderPref.class, 1);
    }

    private static <V, T extends ConfigItemWrapper<?, V>> void mockPreference(Class<T> configItemClass, V value) {
        T prefMock = Mockito.mock(configItemClass);
        Mockito.when(ApplicationContext.INSTANCE.getConfigRegistry().get(configItemClass)).thenReturn(prefMock);
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
    class GeneralOptions {

        @Test
        void shouldCreateMarkWatched() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().markWatched().buildAsList();
            doAssertions(command, "--mark-watched");
        }

        @Test
        void shouldCreateNoContinue() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().noContinue().buildAsList();
            doAssertions(command, "--no-continue");
        }

        @Test
        void shouldCreateNoPart() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().noPart().buildAsList();
            doAssertions(command, "--no-part");
        }

        @Test
        void shouldCreateNoMTime() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().noMTime().buildAsList();
            doAssertions(command, "--no-mtime");
        }

        @Test
        void shouldCreateCookies() {
            Path path = Path.of("path");
            List<String> command = YoutubeDlCommandBuilder.newInstance().cookiesFile(path).buildAsList();
            doAssertions(command, "--cookies", path.toString());
        }
    }

    @Nested
    @DisplayName("Network options")
    class NetworkOptions {

        @Test
        void shouldCreateProxyUrl() {
            String url = "http://proxy";
            List<String> command = YoutubeDlCommandBuilder.newInstance().proxy(url).buildAsList();
            doAssertions(command, "--proxy", url);
        }

        @Test
        void shouldCreateSocketTimeout() {
            int timeout = 10;
            List<String> command = YoutubeDlCommandBuilder.newInstance().socketTimeout(timeout).buildAsList();
            doAssertions(command, "--socket-timeout", String.valueOf(timeout));
        }

        @Test
        void shouldCreateSourceAddress() {
            String ip = "127.0.0.1";
            List<String> command = YoutubeDlCommandBuilder.newInstance().sourceAddress(ip).buildAsList();
            doAssertions(command, "--source-address", ip);
        }

        @Test
        void shouldCreateForceIpV4() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().forceIpV4().buildAsList();
            doAssertions(command, "--force-ipv4");
        }

        @Test
        void shouldCreateForceIpV6() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().forceIpV6().buildAsList();
            doAssertions(command, "--force-ipv6");
        }
    }

    @Nested
    @DisplayName("Download options")
    class DownloadOptions {
        @Test
        void shouldCreateRateLimit() {
            String limit = "4.2M";
            List<String> command = YoutubeDlCommandBuilder.newInstance().rateLimit(limit).buildAsList();
            doAssertions(command, "-r", limit);
        }
    }

    @Nested
    @DisplayName("Authentication options")
    class AuthenticationOptions {

        @Test
        void shouldCreateUsername() {
            String username = "usr";
            List<String> command = YoutubeDlCommandBuilder.newInstance().username(username).buildAsList();
            doAssertions(command, "-u", username);
        }

        @Test
        void shouldCreatePassword() {
            String password = "pass";
            List<String> command = YoutubeDlCommandBuilder.newInstance().password(password).buildAsList();
            doAssertions(command, "-p", password);
        }

        @Test
        void shouldCreateTwoFactorCode() {
            String code = "pass";
            List<String> command = YoutubeDlCommandBuilder.newInstance().twoFactor(code).buildAsList();
            doAssertions(command, "-2", code);
        }

        @Test
        void shouldCreateNetrc() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().useNetrc().buildAsList();
            doAssertions(command, "--netrc");
        }

        @Test
        void shouldCreateVideoPassword() {
            String password = "pass";
            List<String> command = YoutubeDlCommandBuilder.newInstance().videoPassword(password).buildAsList();
            doAssertions(command, "--video-password", password);
        }
    }
}
