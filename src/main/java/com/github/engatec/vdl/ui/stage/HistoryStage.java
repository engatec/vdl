package com.github.engatec.vdl.ui.stage;

import com.github.engatec.vdl.controller.history.HistoryController;
import com.github.engatec.vdl.core.ApplicationContext;
import javafx.stage.Stage;
import javafx.util.Callback;

public class HistoryStage extends AppStage {

    public HistoryStage() {
        init();
        stage.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.history.title"));
        stage.setWidth(900);
        stage.setHeight(600);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/history/history.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new HistoryController(stage);
    }
}
