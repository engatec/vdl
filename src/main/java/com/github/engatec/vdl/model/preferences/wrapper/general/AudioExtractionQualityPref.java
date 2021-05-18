package com.github.engatec.vdl.model.preferences.wrapper.general;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.general.AudioExtractionQualityConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AudioExtractionQualityPref extends ConfigItemWrapper<IntegerProperty, Integer> {

    private static final ConfigItem<Integer> CONFIG_ITEM = new AudioExtractionQualityConfigItem();

    private final IntegerProperty property = new SimpleIntegerProperty();

    public AudioExtractionQualityPref() {
        restore();
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
