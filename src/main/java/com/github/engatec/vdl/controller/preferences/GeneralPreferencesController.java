package com.github.engatec.vdl.controller.preferences;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.engatec.fxcontrols.FxDirectoryChooser;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.AudioFormat;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.Resolution;
import com.github.engatec.vdl.model.preferences.general.AutoSelectFormatConfigItem;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AudioExtractionFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AudioExtractionQualityPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSearchFromClipboardPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSelectFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YoutubeDlStartupUpdatesCheckPref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.data.ComboBoxValueHolder;
import com.github.engatec.vdl.validation.InputForm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

public class GeneralPreferencesController extends ScrollPane implements InputForm {

    @FXML private ComboBox<ComboBoxValueHolder<Language>> languageComboBox;

    @FXML private CheckBox youtubeDlStartupUpdatesCheckBox;

    private final ToggleGroup downloadPathRadioGroup = new ToggleGroup();
    @FXML private RadioButton downloadPathRadioBtn;
    @FXML private RadioButton askPathRadioBtn;
    @FXML private FxDirectoryChooser downloadPathDirectoryChooser;

    @FXML private CheckBox autoSearchFromClipboardCheckBox;

    @FXML private ComboBox<Integer> autoSelectFormatComboBox;

    @FXML private ComboBox<String> audioExtractionFormatComboBox;
    @FXML private Slider audioExtractionQualitySlider;

    @FXML
    public void initialize() {
        initLanguageSettings();
        initDownloadPathSettings();
        initAutoSelectFormatSettings();
        initAudioExtractionSettings();

        bindPropertyHolder();
    }

    private void initLanguageSettings() {
        List<ComboBoxValueHolder<Language>> languages = Stream.of(Language.values())
                .map(it -> new ComboBoxValueHolder<>(it.getLocalizedName(), it))
                .collect(Collectors.toList());
        languageComboBox.getItems().addAll(languages);

        Language currentLanguage = Language.getByLocaleCode(ConfigRegistry.get(LanguagePref.class).getValue());
        languages.stream()
                .filter(it -> it.getValue() == currentLanguage)
                .findFirst()
                .ifPresent(it -> languageComboBox.getSelectionModel().select(it));

        languageComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                Language newLanguage = newValue.getValue();
                ApplicationContext.INSTANCE.setLanguage(newLanguage);
                LanguagePref languagePref = ConfigRegistry.get(LanguagePref.class);
                languagePref.setValue(newLanguage.getLocaleCode());
                languagePref.save();
                Dialogs.info("preferences.general.language.dialog.info");
            }
        });
    }

    private void bindPropertyHolder() {
        askPathRadioBtn.selectedProperty().bindBidirectional(ConfigRegistry.get(AlwaysAskDownloadPathPref.class).getProperty());
        downloadPathDirectoryChooser.pathProperty().bindBidirectional(ConfigRegistry.get(DownloadPathPref.class).getProperty());
        autoSearchFromClipboardCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(AutoSearchFromClipboardPref.class).getProperty());
        autoSelectFormatComboBox.valueProperty().bindBidirectional(ConfigRegistry.get(AutoSelectFormatPref.class).getProperty().asObject());
        audioExtractionFormatComboBox.valueProperty().bindBidirectional(ConfigRegistry.get(AudioExtractionFormatPref.class).getProperty());
        audioExtractionQualitySlider.valueProperty().bindBidirectional(ConfigRegistry.get(AudioExtractionQualityPref.class).getProperty());
        youtubeDlStartupUpdatesCheckBox.selectedProperty().bindBidirectional(ConfigRegistry.get(YoutubeDlStartupUpdatesCheckPref.class).getProperty());
    }

    private void initDownloadPathSettings() {
        downloadPathRadioBtn.setToggleGroup(downloadPathRadioGroup);
        askPathRadioBtn.setToggleGroup(downloadPathRadioGroup);
        downloadPathDirectoryChooser.setButtonText(ApplicationContext.INSTANCE.getResourceBundle().getString("button.directorychoose"));
        downloadPathDirectoryChooser.disableProperty().bind(downloadPathRadioBtn.selectedProperty().not());
        downloadPathRadioBtn.setSelected(true); // Set default value to trigger ToggleGroup. It will be overriden during PropertyHolder binding
    }

    private void initAutoSelectFormatSettings() {
        ObservableList<Integer> comboBoxItems = autoSelectFormatComboBox.getItems();
        comboBoxItems.add(AutoSelectFormatConfigItem.DEFAULT);
        for (Resolution res : Resolution.values()) {
            comboBoxItems.add(res.getHeight());
        }

        final String BEST_FORMAT = ApplicationContext.INSTANCE.getResourceBundle().getString("preferences.general.autoselectformat.best");

        autoSelectFormatComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer object) {
                if (object == null) {
                    return null;
                }

                return object.equals(AutoSelectFormatConfigItem.DEFAULT) ? BEST_FORMAT : object + "p " + Resolution.getDescriptionByHeight(object);
            }

            @Override
            public Integer fromString(String string) {
                return BEST_FORMAT.equals(string) ? AutoSelectFormatConfigItem.DEFAULT : Integer.parseInt(StringUtils.substringBefore(string, "p"));
            }
        });
    }

    private void initAudioExtractionSettings() {
        List<String> audioFormats = Stream.of(AudioFormat.values())
                .map(AudioFormat::toString)
                .collect(Collectors.toList());
        audioExtractionFormatComboBox.setItems(FXCollections.observableArrayList(audioFormats));
    }

    @Override
    public boolean hasErrors() {
        return false;
    }
}
