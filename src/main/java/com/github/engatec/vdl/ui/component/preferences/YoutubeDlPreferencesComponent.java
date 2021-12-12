package com.github.engatec.vdl.ui.component.preferences;

import com.github.engatec.vdl.ui.component.AppComponent;
import com.github.engatec.vdl.ui.controller.stage.preferences.YoutubedlPreferencesController;
import javafx.stage.Stage;

public class YoutubeDlPreferencesComponent extends AppComponent<YoutubedlPreferencesController> {

    public YoutubeDlPreferencesComponent(Stage stage) {
        super(stage);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/preferences/preferences-youtubedl.fxml";
    }

    @Override
    protected YoutubedlPreferencesController getController() {
        return new YoutubedlPreferencesController();
    }
}
