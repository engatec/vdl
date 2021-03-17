package com.github.engatec.vdl.core.preferences.category;

import com.github.engatec.vdl.component.preferences.GeneralPreferencesComponent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GeneralCategory extends Category {

    public GeneralCategory(String title) {
        super(title);
    }

    @Override
    public Node buildCategoryUi(Stage stage) {
        if (super.node == null) {
            readPreferences();
            super.node = new GeneralPreferencesComponent(stage).load();
        }
        return node;
    }

    @Override
    public void readPreferences() {
    }

    @Override
    public void savePreferences() {
    }
}
