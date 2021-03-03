package com.github.engatec.vdl.stage;

import com.github.engatec.vdl.controller.AboutController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AboutStage extends AppStage {

    public AboutStage() {
        load();
        stage.setResizable(false);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/about.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new AboutController(stage);
    }
}
