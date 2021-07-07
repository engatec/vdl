package com.github.engatec.vdl.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class AppUtilsTests {

    @ParameterizedTest
    @CsvSource({
            "https://www.youtube.com/watch?v=5nl7sKwPQo4, https://www.youtube.com/watch?v=5nl7sKwPQo4",
            "https://www.youtube.com/watch?v=5_Dtd49M4o0&list=PLB4brr7vf-P7FRnchYnS4eSwAnMF0y09v, https://www.youtube.com/watch?v=5_Dtd49M4o0",
            "https://www.facebook.com/peopleareawesome/videos/best-videos-of-the-week/1426881677361006, https://www.facebook.com/peopleareawesome/videos/best-videos-of-the-week/1426881677361006",
            "https://www.youtube.com/watch?list=PLyhufYmBlouSZNrhYwFHO6-DFxjK1e5jx&index=89&v=iBAmXbPfvck&t=74s, https://www.youtube.com/watch?v=iBAmXbPfvck",
            "https://www.youtube.com/playlist?list=PLyhufYmBlouQg3yUlEpb8EcGruI_uKvfw, https://www.youtube.com/playlist?list=PLyhufYmBlouQg3yUlEpb8EcGruI_uKvfw",
            "https://www.youtube.com/c/RADIOTAPOK, https://www.youtube.com/c/RADIOTAPOK"
    })
    void shouldNormalizeBaseUrl(String baseUrl, String expected) {
        String actual = AppUtils.normalizeBaseUrl(baseUrl);
        assertThat(actual).isEqualTo(expected);
    }
}
