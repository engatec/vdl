package com.github.engatec.vdl.model.preferences.wrapper.misc;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.misc.HistoryEntriesNumberConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class HistoryEntriesNumberPref extends ConfigItemWrapper<Property<Integer>, Integer> {

    private static final ConfigItem<Integer> CONFIG_ITEM = new HistoryEntriesNumberConfigItem();

    private final Property<Integer> property = new SimpleObjectProperty<>();

    public HistoryEntriesNumberPref() {
        restore();
        property.addListener((observable, oldValue, newValue) -> HistoryEntriesNumberPref.this.save());
    }

    @Override
    protected ConfigItem<Integer> getConfigItem() {
        return CONFIG_ITEM;
    }

    @Override
    public Property<Integer> getProperty() {
        return property;
    }

    @Override
    public Integer getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(Integer value) {
        property.setValue(value);
    }
}
