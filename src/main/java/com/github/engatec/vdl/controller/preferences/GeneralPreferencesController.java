package com.github.engatec.vdl.controller.preferences;

import java.util.ResourceBundle;

import com.github.engatec.fxcontrols.FxDirectoryChooser;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.preferences.data.AutodownloadFormat;
import com.github.engatec.vdl.core.preferences.data.PredefinedFormatCreator;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSearchFromClipboardPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.SkipDownloadableDetailsSearchPref;
import com.github.engatec.vdl.ui.Icons;
import com.github.engatec.vdl.validation.InputForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

public class GeneralPreferencesController extends VBox implements InputForm {

    private final ToggleGroup downloadPathRadioGroup = new ToggleGroup();
    @FXML private RadioButton downloadPathRadioBtn;
    @FXML private RadioButton askPathRadioBtn;
    @FXML private FxDirectoryChooser downloadPathDirectoryChooser;

    @FXML private CheckBox autoSearchFromClipboardCheckBox;

    @FXML private CheckBox autodownloadCheckBox;
    @FXML private VBox autodownloadSettingsWrapperVBox;
    @FXML private ComboBox<AutodownloadFormat> autodownloadFormatComboBox;
    @FXML private TextField autodownloadFormatTextField;
    @FXML private CheckBox skipDownloadableDetailsSearchCheckBox;

    @FXML
    public void initialize() {
        initDownloadPathSettings();
        initAutodownloadSettings();

        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
        askPathRadioBtn.selectedProperty().bindBidirectional(ConfigRegistry.get(AlwaysAskDownloadPathPref.class).getProperty());
        downloadPathDirectoryChooser.pathProperty().bindBidirectional(ConfigRegistry.get(DownloadPathPref.class).getProperty());

        autoSearchFromClipboardCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(AutoSearchFromClipboardPref.class).getProperty());

        autodownloadCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(AutoDownloadPref.class).getProperty());
        autodownloadFormatTextField.textProperty().bindBidirectional(ConfigRegistry.get(AutoDownloadFormatPref.class).getProperty());
        skipDownloadableDetailsSearchCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(SkipDownloadableDetailsSearchPref.class).getProperty());
    }

    private void initDownloadPathSettings() {
        downloadPathRadioBtn.setToggleGroup(downloadPathRadioGroup);
        askPathRadioBtn.setToggleGroup(downloadPathRadioGroup);
        downloadPathDirectoryChooser.setButtonText(ApplicationContext.INSTANCE.getResourceBundle().getString("button.directorychoose"));
        downloadPathDirectoryChooser.disableProperty().bind(downloadPathRadioBtn.selectedProperty().not());
        downloadPathRadioBtn.setSelected(true); // Set default value to trigger ToggleGroup. It will be overriden during PropertyHolder binding
    }

    private void initAutodownloadSettings() {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        autodownloadSettingsWrapperVBox.disableProperty().bind(autodownloadCheckBox.selectedProperty().not());

        autodownloadFormatComboBox.setItems(FXCollections.observableArrayList(
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.best"), PredefinedFormatCreator.create(null)),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher2160p"), PredefinedFormatCreator.create("2160")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher1440p"), PredefinedFormatCreator.create("1440")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher1080p"), PredefinedFormatCreator.create("1080")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher720p"), PredefinedFormatCreator.create("720")),
                new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.nothigher480p"), PredefinedFormatCreator.create("480"))
        ));

        String formatFromPreferences = ConfigRegistry.get(AutoDownloadFormatPref.class).getProperty().getValueSafe();
        ObservableList<AutodownloadFormat> comboboxItems = autodownloadFormatComboBox.getItems();
        String customFormatValue = comboboxItems.stream()
                .filter(it -> it.getValue().equals(formatFromPreferences))
                .findFirst()
                .map(it -> StringUtils.EMPTY)
                .orElse(formatFromPreferences);
        comboboxItems.add(new AutodownloadFormat(resourceBundle.getString("preferences.general.data.autodownload.format.custom"), customFormatValue));

        SingleSelectionModel<AutodownloadFormat> selectionModel = autodownloadFormatComboBox.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) ->
                ConfigRegistry.get(AutoDownloadFormatPref.class).setValue(newValue.getValue())
        );
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

        skipDownloadableDetailsSearchCheckBox.setGraphic(Icons.infoWithTooltip("preferences.general.autodownload.checkbox.skipdetailssearch.tooltip"));
        skipDownloadableDetailsSearchCheckBox.setContentDisplay(ContentDisplay.RIGHT);
    }

    @Override
    public boolean hasErrors() {
        return false;
    }
}
