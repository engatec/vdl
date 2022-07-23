package com.github.engatec.vdl.preference.property.general;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.general.AudioExtractionQualityConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class AudioExtractionQualityConfigProperty extends ConfigProperty<IntegerProperty, Integer> {

    private static final ConfigItem<Integer> CONFIG_ITEM = new AudioExtractionQualityConfigItem();

    private final IntegerProperty property = new SimpleIntegerProperty();

    public AudioExtractionQualityConfigProperty() {
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
