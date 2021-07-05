package com.github.engatec.vdl.model.preferences.youtubedl;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

public class OutputTemplateConfigItem extends YoutubeDlConfigItem<String> {

    public static final String DEFAULT = "%(title).150s %(id).40s %(resolution).10s.%(ext)s";

    @Override
    protected String getName() {
        return "output_template";
    }

    @Override
    public String getValue(Preferences prefs) {
        return prefs.get(getKey(), DEFAULT);
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
