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
        propertyHolder.setAutoSearchFromClipboard(Boolean.parseBoolean(config.getValue(ConfigProperty.AUTO_SEARCH_FROM_CLIPBOARD)));
        propertyHolder.setAutoDownload(Boolean.parseBoolean(config.getValue(ConfigProperty.AUTO_DOWNLOAD)));
        propertyHolder.setAutodownloadUseCustomFormat(Boolean.parseBoolean(config.getValue(ConfigProperty.AUTO_DOWNLOAD_USE_CUSTOM_FORMAT)));
        propertyHolder.setAutodownloadCustomFormat(config.getValue(ConfigProperty.AUTO_DOWNLOAD_CUSTOM_FORMAT));
    }

    @Override
    public void savePreferences() {
        if (propertyHolder == null) {
            return;
        }

        ConfigManager config = ConfigManager.INSTANCE;
        config.setValue(ConfigProperty.DOWNLOAD_ALWAYS_ASK_PATH, String.valueOf(propertyHolder.isAlwaysAskPath()));
        config.setValue(ConfigProperty.DOWNLOAD_PATH, propertyHolder.getDownloadPath());
        config.setValue(ConfigProperty.AUTO_SEARCH_FROM_CLIPBOARD, String.valueOf(propertyHolder.isAutoSearchFromClipboard()));
        config.setValue(ConfigProperty.AUTO_DOWNLOAD, String.valueOf(propertyHolder.isAutoDownload()));
        config.setValue(ConfigProperty.AUTO_DOWNLOAD_USE_CUSTOM_FORMAT, String.valueOf(propertyHolder.isAutodownloadUseCustomFormat()));
        config.setValue(ConfigProperty.AUTO_DOWNLOAD_CUSTOM_FORMAT, propertyHolder.getAutodownloadCustomFormat());
    }
}
