package com.github.engatec.vdl.core.preferences;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

import com.github.engatec.vdl.model.Language;
import org.apache.commons.lang3.SystemUtils;

public enum ConfigProperty {

    DOWNLOAD_PATH("download.path", Paths.get(SystemUtils.getUserHome().getAbsolutePath(), "Downloads").toString()),
    DOWNLOAD_ALWAYS_ASK_PATH("download.alwaysAskPath", "false"),
    LANGUAGE("language", Objects.requireNonNullElse(Locale.getDefault().getLanguage(), Language.ENGLISH.getLocaleLanguage())),
    AUTO_SEARCH_FROM_CLIPBOARD("general.autosearchfromclipboard", "false"),
    NO_M_TIME("youtubedl.noMTime", "true");

    private final String key;
    private final String defaultValue;

    ConfigProperty(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
