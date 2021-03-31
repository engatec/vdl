package com.github.engatec.vdl.model;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public enum AudioFormat {

    MP3, AAC, M4A, FLAC, OPUS, VORBIS, WAV;

    public static AudioFormat getByString(String value) {
        if (StringUtils.isBlank(value)) {
            return MP3;
        }

        for (AudioFormat format : values()) {
            if (value.equalsIgnoreCase(format.name())) {
                return format;
            }
        }
        return MP3;
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
