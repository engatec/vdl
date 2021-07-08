package com.github.engatec.vdl.model.preferences.ui;

import com.github.engatec.vdl.model.preferences.ConfigItem;

public abstract class UiConfigItem<T> extends ConfigItem<T> {

    @Override
    protected String getCategory() {
        return "ui";
    }
}
