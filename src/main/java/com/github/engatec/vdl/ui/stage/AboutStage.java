package com.github.engatec.vdl.ui.stage;

import com.github.engatec.vdl.ui.controller.AboutController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AboutStage extends AppStage {

    public AboutStage() {
        init();
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/about.fxml";
    }

    @Override
    protected void init() {
        super.init();
        stage.setResizable(false);
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new AboutController(stage);
    }
}
