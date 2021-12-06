package com.github.engatec.vdl.ui.component;

import com.github.engatec.vdl.ui.controller.component.SidebarComponentController;
import javafx.stage.Stage;

public class SidebarComponent extends AppComponent<SidebarComponentController> {

    public SidebarComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/sidebar.fxml";
    }

    @Override
    protected SidebarComponentController getController() {
        return new SidebarComponentController();
    }
}
