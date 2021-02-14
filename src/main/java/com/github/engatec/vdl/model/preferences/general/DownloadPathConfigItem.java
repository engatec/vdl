package com.github.engatec.vdl.model.preferences.general;

import java.nio.file.Paths;
import java.util.prefs.Preferences;

import com.github.engatec.vdl.model.preferences.BaseConfigItem;
import com.github.engatec.vdl.model.preferences.ConfigCategory;
import org.apache.commons.lang3.SystemUtils;

public class DownloadPathConfigItem extends BaseConfigItem<String> {

    private static final String DEFAULT = Paths.get(SystemUtils.getUserHome().getAbsolutePath(), "Downloads").toString();

    @Override
    protected ConfigCategory getCategory() {
        return ConfigCategory.GENERAL;
    }

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
