package com.github.engatec.vdl.controller.preferences;

import java.io.File;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.preferences.propertyholder.YoutubedlPropertyHolder;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.ui.Icons;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class YoutubedlPreferencesController extends VBox {

    private final Stage stage;
    private final YoutubedlPropertyHolder propertyHolder;

    @FXML private CheckBox noMTimeCheckBox;

    @FXML private CheckBox useConfigFileCheckBox;
    @FXML private TextField configFileTextField;
    @FXML private Button configFileChooseBtn;

    @FXML private AnchorPane proxyUrlHintPane;

    public YoutubedlPreferencesController(Stage stage, YoutubedlPropertyHolder propertyHolder) {
        this.stage = stage;
        this.propertyHolder = propertyHolder;
    }

    @FXML
    public void initialize() {
        initNetworkSettings();
        initConfigFileSettings();
        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        noMTimeCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(NoMTimePref.class).getProperty());

        useConfigFileCheckBox.selectedProperty().bindBidirectional(propertyHolder.useConfigFileProperty());
        configFileTextField.textProperty().bindBidirectional(propertyHolder.configFilePathProperty());
    }

    private void initNetworkSettings() {
        Group infoIcon = Icons.infoWithTooltip("preferences.youtubedl.network.proxy.hint");
        proxyUrlHintPane.getChildren().add(infoIcon);
        AnchorPane.setTopAnchor(infoIcon, 0.0);
        AnchorPane.setRightAnchor(infoIcon, 0.0);
        AnchorPane.setBottomAnchor(infoIcon, 0.0);
        AnchorPane.setLeftAnchor(infoIcon, 0.0);
    }

    private void initConfigFileSettings() {
        useConfigFileCheckBox.setGraphic(Icons.infoWithTooltip("preferences.youtubedl.checkbox.configitem.tooltip"));
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
