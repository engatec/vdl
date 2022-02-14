package com.github.engatec.vdl.ui.stage.core;

import com.github.engatec.vdl.ui.stage.controller.ProgressDialogController;
import javafx.concurrent.Service;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ProgressDialogStage extends AppStage {

    private final String title;
    private final Service<?> service;

    public ProgressDialogStage(String title, Service<?> service) {
        this.title = title;
        this.service = service;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setResizable(false);
        stage.setTitle(title);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/dialog/progress-dialog.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new ProgressDialogController(stage, service);
    }
}
