package com.github.engatec.vdl.core.youtubedl;

public enum YoutubeDlAttr {

    NO_CODEC("none"),
    UNKNOWN_FORMAT("unknown_video");

    private final String value;

    YoutubeDlAttr(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
