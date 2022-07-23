package com.github.engatec.vdl.preference.configitem.misc;

import com.github.engatec.vdl.preference.configitem.ConfigItem;

public abstract class MiscConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "misc";
    }
}
