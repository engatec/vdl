package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.controller.preferences.PostprocessingPreferencesController;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.propertyholder.PostprocessingPropertyHolder;
import javafx.scene.Node;
import javafx.stage.Stage;

public class PostprocessingCategory extends Category {

    private PostprocessingPropertyHolder propertyHolder;

    public PostprocessingCategory(String title) {
        super(title);
    }

    @Override
    public Node buildCategoryUi(Stage stage) {
        if (super.node == null) {
            readPreferences();
            super.node = UiManager.loadComponent(UiComponent.PREFERENCES_POSTPROCESSING, param -> new PostprocessingPreferencesController(propertyHolder));
        }
        return node;
    }

    @Override
    public void readPreferences() {
        ConfigManager config = ConfigManager.INSTANCE;
    }

    @Override
    public void savePreferences() {
        if (propertyHolder == null) {
            return;
        }
    }
}
