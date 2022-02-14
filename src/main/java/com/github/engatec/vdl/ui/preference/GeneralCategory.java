package com.github.engatec.vdl.ui.preference;

import com.github.engatec.vdl.ui.component.core.preferences.GeneralPreferencesComponent;
import com.github.engatec.vdl.ui.stage.controller.preferences.GeneralPreferencesController;
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
