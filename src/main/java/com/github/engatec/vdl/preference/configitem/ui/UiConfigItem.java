package com.github.engatec.vdl.preference.configitem.ui;

import com.github.engatec.vdl.preference.configitem.ConfigItem;

public abstract class UiConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "ui";
    }
}
