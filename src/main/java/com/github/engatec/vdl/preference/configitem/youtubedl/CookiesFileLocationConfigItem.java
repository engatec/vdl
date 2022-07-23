package com.github.engatec.vdl.preference.configitem.youtubedl;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

public class CookiesFileLocationConfigItem extends YoutubeDlConfigItem<String> {

    @Override
    protected String getName() {
        return "cookiesFileLocation";
    }

    @Override
    public String getValue(Preferences prefs) {
        return prefs.get(getKey(), StringUtils.EMPTY);
    }

    @Override
    public void setValue(Preferences prefs, String value) {
        prefs.put(getKey(), value);
    }
}
