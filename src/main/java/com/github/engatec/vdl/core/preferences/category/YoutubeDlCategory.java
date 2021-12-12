package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.ui.component.preferences.YoutubeDlPreferencesComponent;
import com.github.engatec.vdl.ui.controller.stage.preferences.YoutubedlPreferencesController;
import javafx.scene.Node;
import javafx.stage.Stage;

public class YoutubeDlCategory extends Category {

    public YoutubeDlCategory(String title) {
        super(title);
    }

    @Override
    public Node buildCategoryUi(Stage stage) {
        if (super.node == null) {
            super.node = new YoutubeDlPreferencesComponent(stage).load();
        }
        return node;
    }

    @Override
    public boolean hasErrors() {
        if (super.node == null) {
            return false;
        }

        return ((YoutubedlPreferencesController) node).hasErrors();
    }
}
