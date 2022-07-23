package com.github.engatec.vdl.preference.configitem.general;

import java.util.prefs.Preferences;

public class AudioExtractionAddMetadataConfigItem extends GeneralConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "audio_extraction_add_metadata";
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
