package com.github.engatec.vdl.model.preferences.wrapper.misc;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.misc.HistoryEntriesNumberConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class HistoryEntriesNumberPref extends ConfigItemWrapper<IntegerProperty, Integer> {

    private static final ConfigItem<Integer> CONFIG_ITEM = new HistoryEntriesNumberConfigItem();

    private final IntegerProperty property = new SimpleIntegerProperty();

    public HistoryEntriesNumberPref() {
        restore();
        property.addListener((observable, oldValue, newValue) -> HistoryEntriesNumberPref.this.save());
    }

    @Override
    protected ConfigItem<Integer> getConfigItem() {
        return CONFIG_ITEM;
    }

    @Override
    public IntegerProperty getProperty() {
        return property;
    }

    @Override
    public Integer getValue() {
        return property.get();
    }

    @Override
    public void setValue(Integer value) {
        property.set(value);
    }
}
