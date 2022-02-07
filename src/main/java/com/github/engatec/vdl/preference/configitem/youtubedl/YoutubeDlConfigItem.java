package com.github.engatec.vdl.preference.configitem.youtubedl;

import com.github.engatec.vdl.preference.configitem.ConfigItem;

public abstract class YoutubeDlConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "youtubedl";
    }
}
