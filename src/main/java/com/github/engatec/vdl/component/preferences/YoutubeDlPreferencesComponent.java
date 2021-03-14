package com.github.engatec.vdl.component.preferences;

import com.github.engatec.vdl.component.AppComponent;
import com.github.engatec.vdl.controller.preferences.YoutubedlPreferencesController;
import com.github.engatec.vdl.core.preferences.propertyholder.YoutubedlPropertyHolder;
import javafx.stage.Stage;

public class YoutubeDlPreferencesComponent extends AppComponent<YoutubedlPreferencesController> {

    private final YoutubedlPropertyHolder propertyHolder;

    public YoutubeDlPreferencesComponent(Stage stage, YoutubedlPropertyHolder propertyHolder) {
        super(stage);
        this.propertyHolder = propertyHolder;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/preferences/preferences-youtubedl.fxml";
    }

    @Override
    protected YoutubedlPreferencesController getController() {
        return new YoutubedlPreferencesController(stage, propertyHolder);
    }
}
