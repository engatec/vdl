package com.github.engatec.vdl.model;

import org.apache.commons.lang3.StringUtils;

public enum Resolution {

    UHD_8K(4320, "8K Ultra HD"),
    UHD_4K(2160, "4K Ultra HD"),
    QHD(1440, "2K Quad HD"),
    FULL_HD(1080, "Full HD"),
    HD(720, "HD"),
    SD(480, "SD");

    private final int height;
    private final String description;

    Resolution(int height, String description) {
        this.height = height;
        this.description = description;
    }

    public int getHeight() {
        return height;
    }

    public static String getDescriptionByHeight(int height) {
        int maxHeight = values()[0].height;
        int minHeight = values()[values().length - 1].height;

        if (height > maxHeight) {
            return "Ultra HD";
        }

        if (height < minHeight) {
            return StringUtils.EMPTY;
        }

        for (Resolution item : values()) {
            if (height == item.height) {
                return item.description;
            }
        }

        return StringUtils.EMPTY;
    }
}
