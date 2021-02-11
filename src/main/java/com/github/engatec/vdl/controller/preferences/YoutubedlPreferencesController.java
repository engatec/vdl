package com.github.engatec.vdl.controller.preferences;

import com.github.engatec.vdl.core.preferences.propertyholder.YoutubedlPropertyHolder;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class YoutubedlPreferencesController {

    @FXML private CheckBox noMTimeCheckBox;

    private final YoutubedlPropertyHolder propertyHolder;

    public YoutubedlPreferencesController(YoutubedlPropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    @FXML
    public void initialize() {
        noMTimeCheckBox.selectedProperty().bindBidirectional(propertyHolder.noMTimeProperty());
    }
}
