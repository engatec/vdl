package com.github.engatec.vdl.ui.stage;

import com.github.engatec.vdl.ui.controller.stage.AboutController;
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
        stage.setTitle(ctx.getLocalizedString("stage.about.title"));
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new AboutController(stage);
    }
}
