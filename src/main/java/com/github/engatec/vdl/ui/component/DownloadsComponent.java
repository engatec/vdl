package com.github.engatec.vdl.ui.component;

import com.github.engatec.vdl.ui.controller.component.DownloadsComponentController;
import javafx.stage.Stage;

public class DownloadsComponent extends AppComponent<DownloadsComponentController> {

    public DownloadsComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/downloads/downloads.fxml";
    }

    @Override
    protected DownloadsComponentController getController() {
        return new DownloadsComponentController();
    }
}
