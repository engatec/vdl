package com.github.engatec.vdl.preference.property.engine;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.youtubedl.OutputTemplateConfigItem;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;

public class OutputTemplateConfigProperty extends ConfigProperty<StringProperty, String> {

    private static final ConfigItem<String> CONFIG_ITEM = new OutputTemplateConfigItem();

    private final StringProperty property = new SimpleStringProperty();

    public OutputTemplateConfigProperty() {
        restore();
        property.addListener((observable, oldValue, newValue) -> {
            if (StringUtils.isBlank(newValue)) {
                setValue(OutputTemplateConfigItem.DEFAULT);
            }
        });
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
}
