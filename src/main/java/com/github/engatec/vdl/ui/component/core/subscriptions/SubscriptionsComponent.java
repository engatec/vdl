package com.github.engatec.vdl.ui.component.core.subscriptions;

import com.github.engatec.vdl.ui.component.controller.subscriptions.SubscriptionsComponentController;
import com.github.engatec.vdl.ui.component.core.AppComponent;
import javafx.stage.Stage;

public class SubscriptionsComponent extends AppComponent<SubscriptionsComponentController> {

    public SubscriptionsComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/subscriptions/subscriptions.fxml";
    }

    @Override
    protected SubscriptionsComponentController getController() {
        return new SubscriptionsComponentController(stage);
    }
}
