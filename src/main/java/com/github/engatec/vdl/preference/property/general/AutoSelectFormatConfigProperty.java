package com.github.engatec.vdl.preference.property.general;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.general.AutoSelectFormatConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class AutoSelectFormatConfigProperty extends ConfigProperty<Property<Integer>, Integer> {

    private static final ConfigItem<Integer> CONFIG_ITEM = new AutoSelectFormatConfigItem();

    private final Property<Integer> property = new SimpleObjectProperty<>();

    public AutoSelectFormatConfigProperty() {
        restore();
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
