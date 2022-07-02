package com.github.engatec.vdl.model;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

public enum BitrateType {
    CBR, VBR;

    public static BitrateType fromString(String value) {
        if (StringUtils.isBlank(value)) {
            return CBR;
        }

        for (BitrateType item : values()) {
            if (value.equalsIgnoreCase(item.name())) {
                return item;
            }
        }

        return CBR;
    }

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }
}
