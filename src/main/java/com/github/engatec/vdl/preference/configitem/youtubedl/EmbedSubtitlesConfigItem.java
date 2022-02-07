package com.github.engatec.vdl.preference.configitem.youtubedl;

import java.util.prefs.Preferences;

public class EmbedSubtitlesConfigItem extends YoutubeDlConfigItem<Boolean> {

    @Override
    protected String getName() {
        return "embed_subtitles";
    }

    @Override
    public Boolean getValue(Preferences prefs) {
        return prefs.getBoolean(getKey(), true);
    }

    @Override
    public void setValue(Preferences prefs, Boolean value) {
        prefs.putBoolean(getKey(), value);
    }
}
