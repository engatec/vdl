package com.github.engatec.vdl.preference.property.table;

import com.github.engatec.vdl.preference.configitem.ConfigItem;
import com.github.engatec.vdl.preference.configitem.table.DownloadsTableConfigItem;
import com.github.engatec.vdl.preference.model.TableConfigModel;
import com.github.engatec.vdl.preference.property.ConfigProperty;
import javafx.beans.property.SimpleObjectProperty;

public class DownloadsTableConfigProperty extends ConfigProperty<SimpleObjectProperty<TableConfigModel>, TableConfigModel> {

    private static final ConfigItem<TableConfigModel> CONFIG_ITEM = new DownloadsTableConfigItem();

    private final SimpleObjectProperty<TableConfigModel> property = new SimpleObjectProperty<>();

    public DownloadsTableConfigProperty() {
        restore();
    }

    @Override
    protected ConfigItem<TableConfigModel> getConfigItem() {
        return CONFIG_ITEM;
    }

    @Override
    public SimpleObjectProperty<TableConfigModel> getProperty() {
        return property;
    }

    @Override
    public TableConfigModel getValue() {
        return property.getValue();
    }

    @Override
    public void setValue(TableConfigModel value) {
        property.setValue(value);
    }
}
