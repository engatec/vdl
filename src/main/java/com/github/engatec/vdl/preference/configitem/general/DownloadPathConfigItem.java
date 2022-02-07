package com.github.engatec.vdl.preference.configitem.general;

import java.nio.file.Paths;
import java.util.prefs.Preferences;

import org.apache.commons.lang3.SystemUtils;

public class DownloadPathConfigItem extends GeneralConfigItem<String> {

    private static final String DEFAULT = Paths.get(SystemUtils.getUserHome().getAbsolutePath(), "Downloads").toString();

    @Override
    protected String getName() {
        return "downloadPath";
    }

    @Override
    public String getValue(Preferences prefs) {
        return prefs.get(getKey(), DEFAULT);
    }

    @Override
    public void setValue(Preferences prefs, String value) {
        prefs.put(getKey(), value);
    }
}
