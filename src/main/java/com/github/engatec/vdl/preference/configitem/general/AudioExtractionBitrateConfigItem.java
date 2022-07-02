package com.github.engatec.vdl.preference.configitem.general;

import java.util.prefs.Preferences;

public class AudioExtractionBitrateConfigItem extends GeneralConfigItem<Integer> {

    private static final int DEFAULT_BITRATE = 320;

    @Override
    protected String getName() {
        return "audio_extraction_bitrate";
    }

    @Override
    public Integer getValue(Preferences prefs) {
        return prefs.getInt(getKey(), DEFAULT_BITRATE);
    }

    @Override
    public void setValue(Preferences prefs, Integer value) {
        prefs.putInt(getKey(), value);
    }
}
