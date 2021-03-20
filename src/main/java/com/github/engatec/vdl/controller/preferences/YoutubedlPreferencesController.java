package com.github.engatec.vdl.controller.preferences;

import java.io.File;

import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.handler.textformatter.IntegerTextFormatter;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ConfigFilePathPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.NoMTimePref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.ProxyUrlPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.SocketTimeoutPref;
import com.github.engatec.vdl.model.preferences.wrapper.youtubedl.UseConfigFilePref;
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

    @FXML private TextField proxyUrlTextField;
    @FXML private AnchorPane proxyUrlHintPane;

    @FXML private TextField socketTimoutTextField;

    @FXML private CheckBox noMTimeCheckBox;

    @FXML private CheckBox useConfigFileCheckBox;
    @FXML private TextField configFileTextField;
    @FXML private Button configFileChooseBtn;

    public YoutubedlPreferencesController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        initNetworkSettings();
        initConfigFileSettings();
        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        proxyUrlTextField.textProperty().bindBidirectional(ConfigRegistry.get(ProxyUrlPref.class).getProperty());
        socketTimoutTextField.textProperty().bindBidirectional(ConfigRegistry.get(SocketTimeoutPref.class).getProperty());
        noMTimeCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(NoMTimePref.class).getProperty());

        useConfigFileCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(UseConfigFilePref.class).getProperty());
        configFileTextField.textProperty().bindBidirectional(ConfigRegistry.get(ConfigFilePathPref.class).getProperty());
    }

    private void initNetworkSettings() {
        Group proxyUrlHintIcon = Icons.infoWithTooltip("preferences.youtubedl.network.proxy.hint");
        proxyUrlHintPane.getChildren().add(proxyUrlHintIcon);
        AnchorPane.setTopAnchor(proxyUrlHintIcon, 0.0);
        AnchorPane.setRightAnchor(proxyUrlHintIcon, 0.0);
        AnchorPane.setBottomAnchor(proxyUrlHintIcon, 0.0);
        AnchorPane.setLeftAnchor(proxyUrlHintIcon, 0.0);

        socketTimoutTextField.setTextFormatter(new IntegerTextFormatter());
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
