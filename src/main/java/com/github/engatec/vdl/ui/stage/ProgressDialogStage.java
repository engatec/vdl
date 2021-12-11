package com.github.engatec.vdl.ui.stage;

import com.github.engatec.vdl.ui.controller.dialog.ProgressDialogController;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ProgressDialogStage extends AppStage {

    private final String title;
    private final Task<?> task;
    private final Runnable onSuccessCallback;

    public ProgressDialogStage(String title, Task<?> task, Runnable onSuccessCallback) {
        this.title = title;
        this.task = task;
        this.onSuccessCallback = onSuccessCallback;
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
        return param -> new ProgressDialogController(stage, task, onSuccessCallback);
    }
}
