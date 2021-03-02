package com.github.engatec.vdl.controller.preferences;

import java.io.File;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.propertyholder.YoutubedlPropertyHolder;
import com.github.engatec.vdl.ui.Icons;
import com.github.engatec.vdl.util.Svg;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class YoutubedlPreferencesController extends StageAwareController {

    @FXML private CheckBox noMTimeCheckBox;

    @FXML private CheckBox useConfigFileCheckBox;
    @FXML private TextField configFileTextField;
    @FXML private Button configFileChooseBtn;

    private YoutubedlPropertyHolder propertyHolder;

    private YoutubedlPreferencesController() {
    }

    public YoutubedlPreferencesController(Stage stage, YoutubedlPropertyHolder propertyHolder) {
        super(stage);
        this.propertyHolder = propertyHolder;
    }

    @FXML
    public void initialize() {
        initConfigFileSettings();
        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        noMTimeCheckBox.selectedProperty().bindBidirectional(propertyHolder.noMTimeProperty());

        useConfigFileCheckBox.selectedProperty().bindBidirectional(propertyHolder.useConfigFileProperty());
        configFileTextField.textProperty().bindBidirectional(propertyHolder.configFilePathProperty());
    }

    private void initConfigFileSettings() {
        Group infoIcon = Icons.info();
        Svg.scale(infoIcon, 0.8);
        Tooltip tooltip = new Tooltip(ApplicationContext.INSTANCE.getResourceBundle().getString("preferences.youtubedl.checkbox.configitem.tooltip"));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(infoIcon, tooltip);
        useConfigFileCheckBox.setGraphic(infoIcon);
        useConfigFileCheckBox.setContentDisplay(ContentDisplay.RIGHT);

        BooleanBinding configFileCheckBoxUnselected = useConfigFileCheckBox.selectedProperty().not();
        configFileTextField.disableProperty().bind(configFileCheckBoxUnselected);
        configFileChooseBtn.disableProperty().bind(configFileCheckBoxUnselected);
        configFileChooseBtn.setOnAction(this::handleConfigFileChooseBtnClick);
    }

    private void handleConfigFileChooseBtnClick(ActionEvent event) {
        var fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            String path = selectedFile.getAbsolutePath();
            configFileTextField.setText(path);
        }
        event.consume();
    }
}
