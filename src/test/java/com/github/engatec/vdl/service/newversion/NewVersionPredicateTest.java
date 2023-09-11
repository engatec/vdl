package com.github.engatec.vdl.service.newversion;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class NewVersionPredicateTest {

    @ParameterizedTest
    @CsvSource({
            "1.0, 0.9",
            "1.1, 1.0",
            "1.1.3, 1.1.1",
            "1.1.3.1, 1.1.3",
            "1.1.10, 1.1.9",
            "1.5.3, 1.4.9",
            "1.10.1, 1.9.29",
            "1.10.1, 1.10.0",
            "12, 1.3.5"
    })
    void givenNewAndCurrentVersions_whenNewVersionIsHigher_thenReturnTrue(String newVersion, String currentVersion) {
        boolean result = new NewVersionPredicate().test(newVersion, currentVersion);
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @CsvSource({
            "0.9, 0.9",
            "1.0, 1.0",
            "1.1.3, 1.1.3",
            "1.1.3.1, 1.1.3.1",
            "1.10, 1.10",
            "12, 12"
    })
    void givenNewAndCurrentVersions_whenVersionsEqual_thenReturnFalse(String newVersion, String currentVersion) {
        boolean result = new NewVersionPredicate().test(newVersion, currentVersion);
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "0.9, 1.0",
            "1.0, 1.1",
            "1.1.1, 1.1.3",
            "1.1.3, 1.1.3.1",
            "1.1.9, 1.1.10",
            "1.4.9, 1.5.3",
            "1.9.29, 1.10.1",
            "1.10.0, 1.10.1",
            "1.3.5, 12"
    })
    void givenNewAndCurrentVersions_whenCurrentVersionIsHigher_thenReturnFalse(String newVersion, String currentVersion) {
        boolean result = new NewVersionPredicate().test(newVersion, currentVersion);
        assertThat(result).isFalse();
    }
}
