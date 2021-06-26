package com.github.engatec.vdl.model.preferences.misc;

import java.util.prefs.Preferences;

import org.apache.commons.lang3.StringUtils;

public class RecentDownloadPathConfigItem extends MiscConfigItem<String> {

    @Override
    protected String getName() {
        return "recent_download_path";
    }

    @Override
    public String getValue(Preferences prefs) {
        return prefs.get(getKey(), StringUtils.EMPTY);
    }

    @Override
    public void setValue(Preferences prefs, String value) {
        if (StringUtils.isBlank(value)) {
            prefs.remove(getKey());
        } else {
            prefs.put(getKey(), value);
        }
    }
}
