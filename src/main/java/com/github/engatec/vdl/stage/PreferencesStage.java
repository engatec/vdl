package com.github.engatec.vdl.stage;

import com.github.engatec.vdl.controller.preferences.PreferencesController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PreferencesStage extends AppStage {

    public PreferencesStage() {
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setMinWidth(700);
        stage.setMinHeight(400);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/preferences/preferences.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new PreferencesController(stage);
    }
}
