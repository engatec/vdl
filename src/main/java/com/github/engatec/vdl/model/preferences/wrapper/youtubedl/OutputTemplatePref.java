package com.github.engatec.vdl.model.preferences.wrapper.youtubedl;

import com.github.engatec.vdl.model.preferences.ConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.ConfigItemWrapper;
import com.github.engatec.vdl.model.preferences.youtubedl.OutputTemplateConfigItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;

public class OutputTemplatePref extends ConfigItemWrapper<StringProperty, String> {

    private static final ConfigItem<String> CONFIG_ITEM = new OutputTemplateConfigItem();

    private final StringProperty property = new SimpleStringProperty();

    public OutputTemplatePref() {
        restore();
    }

    @Override
    protected ConfigItem<String> getConfigItem() {
        return CONFIG_ITEM;
    }

    @Override
    public StringProperty getProperty() {
        return property;
    }

    @Override
    public String getValue() {
        return property.get();
    }

    @Override
    public void setValue(String value) {
        property.set(value);
    }

    @Override
    public void save() {
        super.save();
        if (StringUtils.isBlank(getValue())) {
            super.restore();
        }
    }
}
