package com.github.engatec.vdl.model.preferences.misc;

import com.github.engatec.vdl.model.preferences.ConfigItem;

public abstract class MiscConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "misc";
    }
}
