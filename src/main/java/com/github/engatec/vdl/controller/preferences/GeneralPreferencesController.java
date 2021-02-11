package com.github.engatec.vdl.controller.preferences;

import java.io.File;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.ConfigProperty;
import com.github.engatec.vdl.core.preferences.propertyholder.GeneralPropertyHolder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class GeneralPreferencesController {

    private Stage stage;
    private GeneralPropertyHolder propertyHolder;

    private final ToggleGroup downloadRadioGroup = new ToggleGroup();

    @FXML private RadioButton downloadPathRadioBtn;
    @FXML private RadioButton askPathRadioBtn;
    @FXML private TextField downloadPathTextField;
    @FXML private Button chooseDownloadPathBtn;

    private GeneralPreferencesController() {
    }

    public GeneralPreferencesController(Stage stage, GeneralPropertyHolder propertyHolder) {
        this.stage = stage;
        this.propertyHolder = propertyHolder;
    }

    @FXML
    public void initialize() {
        chooseDownloadPathBtn.setOnAction(this::handleDownloadPathChoose);
        initDownloadRadioGroup();

        askPathRadioBtn.selectedProperty().bindBidirectional(propertyHolder.alwaysAskPathProperty());
        downloadPathTextField.textProperty().bindBidirectional(propertyHolder.downloadPathProperty());
    }

    private void initDownloadRadioGroup() {
        downloadPathRadioBtn.setToggleGroup(downloadRadioGroup);
        askPathRadioBtn.setToggleGroup(downloadRadioGroup);
        downloadRadioGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            Toggle selected = observable.getValue();
            if (selected == downloadPathRadioBtn) {
                toggleDownloadPathControls(false);
            } else if (selected == askPathRadioBtn) {
                toggleDownloadPathControls(true);
            }
        });

        String downloadPath = ConfigManager.INSTANCE.getValue(ConfigProperty.DOWNLOAD_PATH);
        downloadPathTextField.setText(downloadPath);

        boolean askDownloadPath = Boolean.parseBoolean(ConfigManager.INSTANCE.getValue(ConfigProperty.DOWNLOAD_ALWAYS_ASK_PATH));
        if (askDownloadPath) {
            askPathRadioBtn.setSelected(true);
        } else {
            downloadPathRadioBtn.setSelected(true);
        }
    }

    private void toggleDownloadPathControls(boolean disabled) {
        downloadPathTextField.setDisable(disabled);
        chooseDownloadPathBtn.setDisable(disabled);
    }

    private void handleDownloadPathChoose(ActionEvent event) {
        var directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            String path = selectedDirectory.getAbsolutePath();
            downloadPathTextField.setText(path);
        }
        event.consume();
    }
}
