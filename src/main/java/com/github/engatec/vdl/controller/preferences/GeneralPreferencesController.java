package com.github.engatec.vdl.controller.preferences;

import java.io.File;
import java.util.ResourceBundle;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.data.AutodownloadFormat;
import com.github.engatec.vdl.core.preferences.data.PredefinedFormatCreator;
import com.github.engatec.vdl.core.preferences.propertyholder.GeneralPropertyHolder;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class GeneralPreferencesController {

    private Stage stage;
    private GeneralPropertyHolder propertyHolder;

    private final ToggleGroup downloadPathRadioGroup = new ToggleGroup();
    @FXML private RadioButton downloadPathRadioBtn;
    @FXML private RadioButton askPathRadioBtn;
    @FXML private TextField downloadPathTextField;
    @FXML private Button chooseDownloadPathBtn;

    @FXML private CheckBox autoSearchFromClipboardCheckBox;

    @FXML private CheckBox autodownloadCheckBox;
    @FXML private VBox autodownloadSettingsWrapperVBox;
    @FXML private ComboBox<AutodownloadFormat> autodownloadFormatComboBox;
    @FXML private TextField autodownloadFormatTextField;
    @FXML private CheckBox skipDownloadableDetailsSearchCheckBox;

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
        autodownloadFormatTextField.textProperty().bindBidirectional(propertyHolder.autodownloadFormatProperty());
        skipDownloadableDetailsSearchCheckBox.selectedProperty().bindBidirectional(propertyHolder.skipDownloadableDetailsSearchProperty());
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
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        autodownloadFormatComboBox.setItems(FXCollections.observableArrayList(
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.best"), PredefinedFormatCreator.create(null)),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher2160p"), PredefinedFormatCreator.create("2160")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher1440p"), PredefinedFormatCreator.create("1440")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher1080p"), PredefinedFormatCreator.create("1080")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher720p"), PredefinedFormatCreator.create("720")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher480p"), PredefinedFormatCreator.create("480"))
        ));

        String formatFromPreferences = propertyHolder.autodownloadFormatProperty().getValueSafe();
        ObservableList<AutodownloadFormat> comboboxItems = autodownloadFormatComboBox.getItems();
        String customFormatValue = comboboxItems.stream()
                .filter(it -> it.getValue().equals(formatFromPreferences))
                .findFirst()
                .map(it -> StringUtils.EMPTY)
                .orElse(formatFromPreferences);
        comboboxItems.add(new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.custom"), customFormatValue));

        SingleSelectionModel<AutodownloadFormat> selectionModel = autodownloadFormatComboBox.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> propertyHolder.setAutodownloadFormat(newValue.getValue()));
        autodownloadFormatTextField.visibleProperty().bind(selectionModel.selectedIndexProperty().isEqualTo(comboboxItems.size() - 1));

        int itemToSelect = 0;
        if (StringUtils.isNotBlank(formatFromPreferences)) {
            for (int i = 0; i < comboboxItems.size(); i++) {
                if (comboboxItems.get(i).getValue().equals(formatFromPreferences)) {
                    itemToSelect = i;
                    break;
                }
            }
        }
        selectionModel.select(itemToSelect);

        autodownloadSettingsWrapperVBox.disableProperty().bind(autodownloadCheckBox.selectedProperty().not());
    }
}
