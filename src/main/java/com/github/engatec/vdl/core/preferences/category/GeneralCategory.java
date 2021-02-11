package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.controller.preferences.GeneralPreferencesController;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.ConfigProperty;
import com.github.engatec.vdl.core.preferences.propertyholder.GeneralPropertyHolder;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GeneralCategory extends Category {

    private GeneralPropertyHolder propertyHolder;

    public GeneralCategory(String title) {
        super(title);
    }

    @Override
    public Node buildCategoryUi(Stage stage) {
        if (super.node == null) {
            readPreferences();
            super.node = UiManager.loadComponent(UiComponent.PREFERENCES_GENERAL, param -> new GeneralPreferencesController(stage, propertyHolder));
        }
        return node;
    }

    @Override
    public void readPreferences() {
        ConfigManager config = ConfigManager.INSTANCE;
        propertyHolder = new GeneralPropertyHolder();
        propertyHolder.setAlwaysAskPath(Boolean.parseBoolean(config.getValue(ConfigProperty.DOWNLOAD_ALWAYS_ASK_PATH)));
        propertyHolder.setDownloadPath(config.getValue(ConfigProperty.DOWNLOAD_PATH));
    }

    @Override
    public void savePreferences() {
        if (propertyHolder == null) {
            return;
        }

        ConfigManager config = ConfigManager.INSTANCE;
        config.setValue(ConfigProperty.DOWNLOAD_ALWAYS_ASK_PATH, String.valueOf(propertyHolder.isAlwaysAskPath()));
        config.setValue(ConfigProperty.DOWNLOAD_PATH, propertyHolder.getDownloadPath());
    }
}
