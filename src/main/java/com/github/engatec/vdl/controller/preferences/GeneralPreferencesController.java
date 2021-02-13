package com.github.engatec.vdl.controller.preferences;

import java.io.File;

import com.github.engatec.vdl.core.preferences.propertyholder.GeneralPropertyHolder;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class GeneralPreferencesController {

    private Stage stage;
    private GeneralPropertyHolder propertyHolder;

    private final ToggleGroup downloadPathRadioGroup = new ToggleGroup();
    @FXML private RadioButton downloadPathRadioBtn;
    @FXML private RadioButton askPathRadioBtn;
    @FXML private TextField downloadPathTextField;
    @FXML private Button chooseDownloadPathBtn;

    @FXML private CheckBox autoSearchFromClipboardCheckBox;

    private final ToggleGroup autodownloadRadioGroup = new ToggleGroup();
    @FXML private CheckBox autodownloadCheckBox;
    @FXML private VBox autodownloadSettingsWrapperVBox;
    @FXML private RadioButton autodownloadBestQualityRadioBtn;
    @FXML private RadioButton autodownloadCustomFormatRadioBtn;
    @FXML private TextField autodownloadCustomFormatTextField;

    private GeneralPreferencesController() {
    }

    public GeneralPreferencesController(Stage stage, GeneralPropertyHolder propertyHolder) {
        this.stage = stage;
        this.propertyHolder = propertyHolder;
    }

    @FXML
    public void initialize() {
        initDownloadPathSettings();
        initAutodownloadSettings();

        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        askPathRadioBtn.selectedProperty().bindBidirectional(propertyHolder.alwaysAskPathProperty());
        downloadPathTextField.textProperty().bindBidirectional(propertyHolder.downloadPathProperty());

        autoSearchFromClipboardCheckBox.selectedProperty().bindBidirectional(propertyHolder.autoSearchFromClipboardProperty());

        autodownloadCheckBox.selectedProperty().bindBidirectional(propertyHolder.autoDownloadProperty());
        autodownloadCustomFormatRadioBtn.selectedProperty().bindBidirectional(propertyHolder.autodownloadUseCustomFormatProperty());
        autodownloadCustomFormatTextField.textProperty().bindBidirectional(propertyHolder.autodownloadCustomFormatProperty());
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

    private void initDownloadPathSettings() {
        chooseDownloadPathBtn.setOnAction(this::handleDownloadPathChoose);

        downloadPathRadioBtn.setToggleGroup(downloadPathRadioGroup);
        askPathRadioBtn.setToggleGroup(downloadPathRadioGroup);
        BooleanProperty downloadPathRadioBtnSelectedProperty = downloadPathRadioBtn.selectedProperty();
        downloadPathTextField.disableProperty().bind(downloadPathRadioBtnSelectedProperty.not());
        chooseDownloadPathBtn.disableProperty().bind(downloadPathRadioBtnSelectedProperty.not());
        downloadPathRadioBtn.setSelected(true); // Set default value to trigger ToggleGroup. It will be overriden during PropertyHolder binding
    }

    private void initAutodownloadSettings() {
        autodownloadSettingsWrapperVBox.disableProperty().bind(autodownloadCheckBox.selectedProperty().not());
        autodownloadBestQualityRadioBtn.setToggleGroup(autodownloadRadioGroup);
        autodownloadCustomFormatRadioBtn.setToggleGroup(autodownloadRadioGroup);
        autodownloadCustomFormatTextField.disableProperty().bind(autodownloadCustomFormatRadioBtn.selectedProperty().not());
        autodownloadBestQualityRadioBtn.setSelected(true); // Set default value to trigger ToggleGroup. It will be overriden during PropertyHolder binding
    }
}
