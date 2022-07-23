package com.github.engatec.vdl.ui.component.core.preferences;

import com.github.engatec.vdl.ui.component.core.AppComponent;
import com.github.engatec.vdl.ui.stage.controller.preferences.UpdatesPreferencesController;
import javafx.stage.Stage;

public class UpdatesPreferencesComponent extends AppComponent<UpdatesPreferencesController> {

    public UpdatesPreferencesComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/preferences/preferences-updates.fxml";
    }

    @Override
    protected UpdatesPreferencesController getController() {
        return new UpdatesPreferencesController();
    }
}
