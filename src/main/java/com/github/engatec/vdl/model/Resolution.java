package com.github.engatec.vdl.model;

import org.apache.commons.lang3.StringUtils;

public enum Resolution {

    // Don't trust the resolutions will be common ones, so make assumptions with ranges
    UHD_8K(3200, 5000, "8K Ultra HD"), // 4320
    UHD_4K(1800, 3200, "4K Ultra HD"), // 2160
    QHD(1300, 1800, "2K Quad HD"), // 1440
    FULL_HD(960, 1300, "Full HD"), // 1080
    HD(680, 960, "HD"), // 720
    SD(420, 680, "SD"); // 480

    private final int minHeight;
    private final int maxHeight;
    private final String description;

    Resolution(int minHeight, int maxHeight, String description) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.description = description;
    }

    public static String getDescriptionByHeight(int height) {
        int maxHeight = values()[0].maxHeight;
        int minHeight = values()[values().length - 1].minHeight;

        if (height > maxHeight) {
            return "Ultra HD";
        }

        if (height < minHeight) {
            return StringUtils.EMPTY;
        }

        for (Resolution item : values()) {
            if (item.minHeight <= height && height < item.maxHeight) {
                return item.description;
            }
        }

        return StringUtils.EMPTY;
    }
}
