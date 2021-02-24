package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

public class AutoDownloadFormatConfigItem extends GeneralConfigItem<String> {

    @Override
    protected String getName() {
        return "autodownloadFormat";
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
