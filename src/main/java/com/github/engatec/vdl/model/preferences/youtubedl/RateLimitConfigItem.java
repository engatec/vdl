package com.github.engatec.vdl.model.preferences.youtubedl;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

public class RateLimitConfigItem extends YoutubeDlConfigItem<String> {

    public static final String DEFAULT = "0"; // No limit

    @Override
    protected String getName() {
        return "rate_limit";
    }

    @Override
    public String getValue(Preferences prefs) {
        return prefs.get(getKey(), DEFAULT);
    }

    @Override
    public void setValue(Preferences prefs, String value) {
        if (StringUtils.isBlank(value)) {
            value = DEFAULT;
        }
        prefs.put(getKey(), StringUtils.strip(value));
    }
}
