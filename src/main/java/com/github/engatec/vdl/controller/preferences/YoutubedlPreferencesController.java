package com.github.engatec.vdl.controller.preferences;

import com.github.engatec.vdl.core.preferences.propertyholder.YoutubedlPropertyHolder;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class YoutubedlPreferencesController {

    @FXML private CheckBox noMTimeCheckBox;

    @FXML private CheckBox useCustomArgumentsCheckBox;
    @FXML private TextField customArgumentsTextField;

    private YoutubedlPropertyHolder propertyHolder;

    private YoutubedlPreferencesController() {
    }

    public YoutubedlPreferencesController(YoutubedlPropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    @FXML
    public void initialize() {
        initCustomArgumentsSettings();
        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        noMTimeCheckBox.selectedProperty().bindBidirectional(propertyHolder.noMTimeProperty());

        useCustomArgumentsCheckBox.selectedProperty().bindBidirectional(propertyHolder.useCustomArgumentsProperty());
        customArgumentsTextField.textProperty().bindBidirectional(propertyHolder.customArgumentsProperty());
    }

    private void initCustomArgumentsSettings() {
        customArgumentsTextField.disableProperty().bind(useCustomArgumentsCheckBox.selectedProperty().not());
    }
}
