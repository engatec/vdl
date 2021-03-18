package com.github.engatec.vdl.stage;

import com.github.engatec.vdl.controller.QueueController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class QueueStage extends AppStage {

    public QueueStage() {
        init();
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/queue/queue.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new QueueController(stage);
    }
}
