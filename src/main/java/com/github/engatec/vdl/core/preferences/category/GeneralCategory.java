package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.ui.component.preferences.GeneralPreferencesComponent;
import com.github.engatec.vdl.ui.controller.stage.preferences.GeneralPreferencesController;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GeneralCategory extends Category {

    public GeneralCategory(String title) {
        super(title);
    }

    @Override
    public Node buildCategoryUi(Stage stage) {
        if (super.node == null) {
            super.node = new GeneralPreferencesComponent(stage).load();
        }
        return node;
    }

    @Override
    public boolean hasErrors() {
        if (super.node == null) {
            return false;
        }

        return ((GeneralPreferencesController) node).hasErrors();
    }
}
