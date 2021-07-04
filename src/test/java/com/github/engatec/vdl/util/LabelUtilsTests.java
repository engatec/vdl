package com.github.engatec.vdl.util;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class LabelUtilsTests {

    @ParameterizedTest
    @MethodSource("fpsProvider")
    void formatFpsTest(Double fps, String expectedString) {
        String actualString = LabelUtils.formatFps(fps);
        assertThat(actualString).isEqualTo(expectedString);
    }

    static Stream<Arguments> fpsProvider() {
        return Stream.of(
                arguments(null, "N/A"),
                arguments(25.0, "25"),
                arguments(25d, "25"),
                arguments(23.98, "23.98")
        );
    }
}
