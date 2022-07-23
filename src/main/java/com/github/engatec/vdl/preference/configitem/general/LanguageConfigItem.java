package com.github.engatec.vdl.preference.configitem.general;

import java.util.Locale;
import java.util.prefs.Preferences;

import com.github.engatec.vdl.model.Language;
import org.apache.commons.lang3.StringUtils;

public class LanguageConfigItem extends GeneralConfigItem<String> {

    private static final String DEFAULT = StringUtils.defaultIfBlank(Locale.getDefault().getLanguage(), Language.ENGLISH.getLocaleCode());

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
