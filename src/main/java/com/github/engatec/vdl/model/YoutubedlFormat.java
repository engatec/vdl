package com.github.engatec.vdl.model;

public enum YoutubedlFormat {

    BEST_AUDIO("bestaudio");

    private final String cmdValue;

    YoutubedlFormat(String cmdValue) {
        this.cmdValue = cmdValue;
    }

    public String getCmdValue() {
        return cmdValue;
    }
}
