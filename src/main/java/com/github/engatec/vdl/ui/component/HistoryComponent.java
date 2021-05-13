package com.github.engatec.vdl.ui.component;

import com.github.engatec.vdl.controller.component.history.HistoryComponentController;
import javafx.stage.Stage;

public class HistoryComponent extends AppComponent<HistoryComponentController> {

    public HistoryComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/history/history.fxml";
    }

    @Override
    protected HistoryComponentController getController() {
        return new HistoryComponentController();
    }
}
