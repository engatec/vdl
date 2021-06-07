package com.github.engatec.vdl.model;

import java.util.Locale;

public enum Language {

    ENGLISH("en", "English"),
    RUSSIAN("ru", "Русский");
    // UKRAINIAN("uk", "Українська");

    private final String localeCode;
    private final String localizedName;

    Language(String localeCode, String localizedName) {
        this.localizedName = localizedName;
        this.localeCode = localeCode;
    }

    public Locale getLocale() {
        return new Locale(localeCode);
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public static Language getByLocaleCode(String localeCode) {
        Language result = ENGLISH;
        for (Language value : values()) {
            if (value.localeCode.equalsIgnoreCase(localeCode)) {
                result = value;
                break;
            }
        }
        return result;
    }

    public String getLocalizedName() {
        return localizedName;
    }
}
