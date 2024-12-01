package com.github.engatec.vdl.core.youtubedl;

import com.github.engatec.vdl.TestHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

public class YoutubeDlCommandBuilderTests {

    @BeforeAll
    static void setUp() {
        TestHelper.initTestApplicationContext();
    }

    private void doAssertions(List<String> command, String key) {
        assertThat(command).hasSize(4);
        assertThat(command)
                .contains("--compat-options", atIndex(1))
                .contains("all,-allow-unsafe-ext", atIndex(2))
                .contains(key, atIndex(3));
    }

    private void doAssertions(List<String> command, String key, String value) {
        assertThat(command).hasSize(5);
        assertThat(command)
                .contains("--compat-options", atIndex(1))
                .contains("all,-allow-unsafe-ext", atIndex(2))
                .contains(key, atIndex(3))
                .contains(value, atIndex(4));
    }

    @Nested
    @DisplayName("General options")
    class GeneralOptionsTests {

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
    class NetworkOptionsTests {

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
    class DownloadOptionsTests {
        @Test
        void shouldCreateRateLimit() {
            String limit = "4.2M";
            List<String> command = YoutubeDlCommandBuilder.newInstance().rateLimit(limit).buildAsList();
            doAssertions(command, "-r", limit);
        }

        @Test
        void shouldCreateSkipDownload() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().skipDownload().buildAsList();
            doAssertions(command, "--skip-download");
        }
    }

    @Nested
    @DisplayName("Subtitles options")
    class SubtutlesOptionsTests {
        @Test
        void shouldCreateWriteSubWithSubLang() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().writeSub(Set.of("en", "ru")).buildAsList();
            assertThat(command)
                    .hasSize(6)
                    .containsAll(List.of("--write-sub", "--sub-lang"))
                    .containsAnyOf("en,ru", "ru,en"); // Since set is passed, can't be sure what order is going to be when joining the elements
        }

        @Test
        void shouldCreateWriteSubAndAllSubsWithoutSubLang() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().writeSub(Set.of()).buildAsList();
            assertThat(command)
                    .hasSize(5)
                    .contains("--write-sub", "--all-subs");
        }

        @Test
        void shouldCreateEmbedSub() {
            List<String> command = YoutubeDlCommandBuilder.newInstance().embedSub().buildAsList();
            doAssertions(command, "--embed-subs");
        }

        @Test
        void shouldCreateConvertSub() {
            String value = "srt";
            List<String> command = YoutubeDlCommandBuilder.newInstance().convertSub(value).buildAsList();
            doAssertions(command, "--convert-subs", value);
        }
    }

    @Nested
    @DisplayName("Authentication options")
    class AuthenticationOptionsTests {

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
