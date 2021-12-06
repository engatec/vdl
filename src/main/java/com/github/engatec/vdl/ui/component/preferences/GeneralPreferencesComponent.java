package com.github.engatec.vdl.ui.component.preferences;

import com.github.engatec.vdl.ui.component.AppComponent;
import com.github.engatec.vdl.ui.controller.preferences.GeneralPreferencesController;
import javafx.stage.Stage;

public class GeneralPreferencesComponent extends AppComponent<GeneralPreferencesController> {

    public GeneralPreferencesComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/preferences/preferences-general.fxml";
    }

    @Override
    protected GeneralPreferencesController getController() {
        return new GeneralPreferencesController();
    }
}
