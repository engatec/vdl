package com.github.engatec.vdl.stage;

import com.github.engatec.vdl.controller.dialog.ProgressDialogController;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ProgressDialogStage extends AppStage {

    private final String title;
    private final Task<?> task;

    public ProgressDialogStage(String title, Task<?> task) {
        this.title = title;
        this.task = task;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setResizable(false);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/dialog/progress-dialog.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new ProgressDialogController(stage, title, task);
    }
}
