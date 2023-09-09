package com.github.engatec.vdl.ui.component.core.preferences;

import com.github.engatec.vdl.ui.component.core.AppComponent;
import com.github.engatec.vdl.ui.stage.controller.preferences.EnginePreferencesController;
import javafx.stage.Stage;

public class EnginePreferencesComponent extends AppComponent<EnginePreferencesController> {

    public EnginePreferencesComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/preferences/preferences-engine.fxml";
    }

    @Override
    protected EnginePreferencesController getController() {
        return new EnginePreferencesController();
    }
}
