package com.github.engatec.vdl.ui.component;

import com.github.engatec.vdl.controller.component.ServicebarComponentController;
import javafx.stage.Stage;

public class ServicebarComponent extends AppComponent<ServicebarComponentController> {

    public ServicebarComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/servicebar.fxml";
    }

    @Override
    protected ServicebarComponentController getController() {
        return new ServicebarComponentController(stage);
    }
}
