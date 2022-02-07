package com.github.engatec.vdl.preference.configitem.general;

import com.github.engatec.vdl.preference.configitem.ConfigItem;

public abstract class GeneralConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "general";
    }
}
