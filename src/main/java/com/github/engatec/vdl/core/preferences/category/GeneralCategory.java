package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.controller.preferences.GeneralPreferencesController;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.propertyholder.GeneralPropertyHolder;
import com.github.engatec.vdl.model.preferences.general.AlwaysAskDownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadFormatConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoSearchFromClipboardConfigItem;
import com.github.engatec.vdl.model.preferences.general.DownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.general.SkipDownloadableDetailsSearchConfigItem;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

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
        propertyHolder.setAlwaysAskPath(config.getValue(new AlwaysAskDownloadPathConfigItem()));
        propertyHolder.setDownloadPath(config.getValue(new DownloadPathConfigItem()));
        propertyHolder.setAutoSearchFromClipboard(config.getValue(new AutoSearchFromClipboardConfigItem()));
        propertyHolder.setAutoDownload(config.getValue(new AutoDownloadConfigItem()));
        propertyHolder.setAutodownloadFormat(config.getValue(new AutoDownloadFormatConfigItem()));
        propertyHolder.setSkipDownloadableDetailsSearch(config.getValue(new SkipDownloadableDetailsSearchConfigItem()));
    }

    @Override
    public void savePreferences() {
        if (propertyHolder == null) {
            return;
        }

        fixState();

        ConfigManager config = ConfigManager.INSTANCE;
        config.setValue(new AlwaysAskDownloadPathConfigItem(), propertyHolder.isAlwaysAskPath());
        config.setValue(new DownloadPathConfigItem(), propertyHolder.getDownloadPath());
        config.setValue(new AutoSearchFromClipboardConfigItem(), propertyHolder.isAutoSearchFromClipboard());
        config.setValue(new AutoDownloadConfigItem(), propertyHolder.isAutoDownload());
        config.setValue(new AutoDownloadFormatConfigItem(), propertyHolder.getAutodownloadFormat());
        config.setValue(new SkipDownloadableDetailsSearchConfigItem(), propertyHolder.isSkipDownloadableDetailsSearch());
    }

    private void fixState() {
        if (StringUtils.isBlank(propertyHolder.getAutodownloadFormat())) {
            propertyHolder.setAutodownloadFormat(StringUtils.EMPTY); // Reset the field if user entered multiple space characters
        }

        if (propertyHolder.isAutoDownload() && StringUtils.isBlank(propertyHolder.getAutodownloadFormat())) {
            propertyHolder.setAutoDownload(false);
        }
    }
}
