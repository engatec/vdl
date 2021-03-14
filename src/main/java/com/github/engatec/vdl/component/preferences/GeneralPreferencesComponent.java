package com.github.engatec.vdl.component.preferences;

import com.github.engatec.vdl.component.AppComponent;
import com.github.engatec.vdl.controller.preferences.GeneralPreferencesController;
import com.github.engatec.vdl.core.preferences.propertyholder.GeneralPropertyHolder;
import javafx.stage.Stage;

public class GeneralPreferencesComponent extends AppComponent<GeneralPreferencesController> {

    private final GeneralPropertyHolder propertyHolder;

    public GeneralPreferencesComponent(Stage stage, GeneralPropertyHolder propertyHolder) {
        super(stage);
        this.propertyHolder = propertyHolder;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/preferences/preferences-general.fxml";
    }

    @Override
    protected GeneralPreferencesController getController() {
        return new GeneralPreferencesController(stage, propertyHolder);
    }
}
