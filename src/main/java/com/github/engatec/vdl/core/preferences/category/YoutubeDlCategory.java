package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.component.preferences.YoutubeDlPreferencesComponent;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.propertyholder.YoutubedlPropertyHolder;
import com.github.engatec.vdl.model.preferences.youtubedl.ConfigFilePathConfigItem;
import com.github.engatec.vdl.model.preferences.youtubedl.NoMTimeConfigItem;
import com.github.engatec.vdl.model.preferences.youtubedl.UseConfigFileConfigItem;
import javafx.scene.Node;
import javafx.stage.Stage;

public class YoutubeDlCategory extends Category {

    private YoutubedlPropertyHolder propertyHolder;

    public YoutubeDlCategory(String title) {
        super(title);
    }

    @Override
    public Node buildCategoryUi(Stage stage) {
        if (super.node == null) {
            readPreferences();
            super.node = new YoutubeDlPreferencesComponent(stage, propertyHolder).load();
        }
        return node;
    }

    @Override
    public void readPreferences() {
        ConfigManager config = ConfigManager.INSTANCE;
        propertyHolder = new YoutubedlPropertyHolder();
        propertyHolder.setNoMTime(config.getValue(new NoMTimeConfigItem()));
        propertyHolder.setUseConfigFile(config.getValue(new UseConfigFileConfigItem()));
        propertyHolder.setConfigFilePath(config.getValue(new ConfigFilePathConfigItem()));
    }

    @Override
    public void savePreferences() {
        if (propertyHolder == null) {
            return;
        }

        ConfigManager config = ConfigManager.INSTANCE;
        config.setValue(new NoMTimeConfigItem(), propertyHolder.isNoMTime());
        config.setValue(new UseConfigFileConfigItem(), propertyHolder.isUseConfigFile());
        config.setValue(new ConfigFilePathConfigItem(), propertyHolder.getConfigFilePath());
    }
}
