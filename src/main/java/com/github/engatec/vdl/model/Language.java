package com.github.engatec.vdl.model;

import java.util.Locale;

public enum Language {

    ENGLISH("en"),
    RUSSIAN("ru"),
    UKRAINIAN("uk");

    private final String localeLanguage;

    Language(String localeLanguage) {
        this.localeLanguage = localeLanguage;
    }

    public Locale getLocale() {
        return new Locale(localeLanguage);
    }

    public String getLocaleLanguage() {
        return localeLanguage;
    }

    public static Language getByLocaleLanguage(String localeLanguage) {
        Language result = ENGLISH;
        for (Language value : values()) {
            if (value.localeLanguage.equalsIgnoreCase(localeLanguage)) {
                result = value;
                break;
            }
        }
        return result;
    }
}
