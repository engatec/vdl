package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.controller.preferences.YoutubedlPreferencesController;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.ConfigProperty;
import com.github.engatec.vdl.core.preferences.propertyholder.YoutubedlPropertyHolder;
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
            super.node = UiManager.loadComponent(UiComponent.PREFERENCES_YOUTUBE_DL, param -> new YoutubedlPreferencesController(propertyHolder));
        }
        return node;
    }

    @Override
    public void readPreferences() {
        ConfigManager config = ConfigManager.INSTANCE;
        propertyHolder = new YoutubedlPropertyHolder();
        propertyHolder.setNoMTime(Boolean.parseBoolean(config.getValue(ConfigProperty.NO_M_TIME)));
    }

    @Override
    public void savePreferences() {
        if (propertyHolder == null) {
            return;
        }

        ConfigManager config = ConfigManager.INSTANCE;
        config.setValue(ConfigProperty.NO_M_TIME, String.valueOf(propertyHolder.isNoMTime()));
    }
}
