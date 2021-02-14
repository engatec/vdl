package com.github.engatec.vdl.model.preferences.general;

import java.util.Locale;
import java.util.prefs.Preferences;

import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.BaseConfigItem;
import com.github.engatec.vdl.model.preferences.ConfigCategory;
import org.apache.commons.lang3.StringUtils;

public class LanguageConfigItem extends BaseConfigItem<String> {

    private static final String DEFAULT = StringUtils.defaultIfBlank(Locale.getDefault().getLanguage(), Language.ENGLISH.getLocaleLanguage());

    @Override
    protected ConfigCategory getCategory() {
        return ConfigCategory.GENERAL;
    }

    @Override
    protected String getName() {
        return "language";
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
