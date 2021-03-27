package com.github.engatec.vdl.core.youtubedl;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

public class YoutubeDlCommandBuilderTests {

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
