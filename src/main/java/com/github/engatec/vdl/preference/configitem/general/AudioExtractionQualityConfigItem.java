package com.github.engatec.vdl.preference.configitem.general;

import java.util.prefs.Preferences;

import com.github.engatec.vdl.model.AudioFormat;

public class AudioExtractionQualityConfigItem extends GeneralConfigItem<Integer> {

    @Override
    protected String getName() {
        return "audio_extraction_quality";
    }

    @Override
    public Integer getValue(Preferences prefs) {
        return prefs.getInt(getKey(), AudioFormat.BEST_QUALITY);
    }

    @Override
    public void setValue(Preferences prefs, Integer value) {
        prefs.putInt(getKey(), value);
    }
}
