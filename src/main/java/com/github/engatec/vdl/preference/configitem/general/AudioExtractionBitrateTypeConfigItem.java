package com.github.engatec.vdl.preference.configitem.general;

import java.util.prefs.Preferences;

import com.github.engatec.vdl.model.BitrateType;

public class AudioExtractionBitrateTypeConfigItem extends GeneralConfigItem<String> {

    @Override
    protected String getName() {
        return "audio_extraction_bitrate_type";
    }

    @Override
    public String getValue(Preferences prefs) {
        return prefs.get(getKey(), BitrateType.CBR.toString());
    }

    @Override
    public void setValue(Preferences prefs, String value) {
        prefs.put(getKey(), value);
    }
}
