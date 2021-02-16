package com.github.engatec.vdl.model.preferences.general;

import com.github.engatec.vdl.model.preferences.ConfigItem;

public abstract class GeneralConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "general";
    }
}
