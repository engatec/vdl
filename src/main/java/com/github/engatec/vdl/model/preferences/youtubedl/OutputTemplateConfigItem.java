package com.github.engatec.vdl.model.preferences.youtubedl;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

public class OutputTemplateConfigItem extends YoutubeDlConfigItem<String> {

    private static final String DEFAULT_VALUE = "%(title).200s.%(ext)s";

    @Override
    protected String getName() {
        return "output_template";
    }

    @Override
    public String getValue(Preferences prefs) {
        return prefs.get(getKey(), DEFAULT_VALUE);
    }

    @Override
    public void setValue(Preferences prefs, String value) {
        if (StringUtils.isBlank(value)) {
            prefs.remove(getKey());
        } else {
            prefs.put(getKey(), StringUtils.strip(value));
        }
    }
}
