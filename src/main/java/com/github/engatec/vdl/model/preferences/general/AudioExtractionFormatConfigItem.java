package com.github.engatec.vdl.model.preferences.general;

import java.util.prefs.Preferences;

import com.github.engatec.vdl.model.AudioFormat;

public class AudioExtractionFormatConfigItem extends GeneralConfigItem<String> {

    private static final String DEFAULT = AudioFormat.MP3.toString();

    @Override
    protected String getName() {
        return "audio_extraction_format";
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
