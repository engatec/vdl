package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

import com.github.engatec.vdl.model.preferences.BaseConfigItem;
import com.github.engatec.vdl.model.preferences.ConfigCategory;

public class AutoSearchFromClipboardConfigItem extends BaseConfigItem<Boolean> {

    @Override
    protected ConfigCategory getCategory() {
        return ConfigCategory.GENERAL;
    }

    @Override
    protected String getName() {
        return "autoSearchFromClipboard";
    }

    @Override
    public Boolean getValue(Preferences prefs) {
        return prefs.getBoolean(getKey(), false);
    }

    @Override
    public void setValue(Preferences prefs, Boolean value) {
        prefs.putBoolean(getKey(), value);
    }
}
