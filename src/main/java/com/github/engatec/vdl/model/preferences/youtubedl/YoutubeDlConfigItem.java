package com.github.engatec.vdl.model.preferences.youtubedl;

import com.github.engatec.vdl.model.preferences.ConfigItem;

public abstract class YoutubeDlConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "youtubedl";
    }
}
