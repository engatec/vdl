package com.github.engatec.vdl.preference.ui;

import com.github.engatec.vdl.ui.component.preferences.UpdatesPreferencesComponent;
import com.github.engatec.vdl.ui.controller.stage.preferences.UpdatesPreferencesController;
import javafx.scene.Node;
import javafx.stage.Stage;

public class UpdatesCategory extends Category {

    public UpdatesCategory(String title) {
        super(title);
    }

    @Override
    public Node buildCategoryUi(Stage stage) {
        if (super.node == null) {
            super.node = new UpdatesPreferencesComponent(stage).load();
        }
        return node;
    }

    @Override
    public boolean hasErrors() {
        if (super.node == null) {
            return false;
        }

        return ((UpdatesPreferencesController) node).hasErrors();
    }
}
