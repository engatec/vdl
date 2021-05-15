package com.github.engatec.vdl.controller.component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.ui.data.ComboBoxValueHolder;
import com.github.engatec.vdl.ui.stage.AboutStage;
import com.github.engatec.vdl.ui.stage.PreferencesStage;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ServicebarComponentController extends HBox {

    private final Stage stage;

    @FXML private ComboBox<ComboBoxValueHolder<Language>> languageComboBox;
    @FXML private ImageView helpButton;
    @FXML private ImageView preferencesButton;

    public ServicebarComponentController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        initLanguages();

        helpButton.setOnMouseClicked(e -> {
            new AboutStage().modal(stage).showAndWait();
            e.consume();
        });

        preferencesButton.setOnMouseClicked(e -> {
            new PreferencesStage().modal(stage).showAndWait();
            e.consume();
        });
    }

    private void initLanguages() {
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
            }
        });
    }
}
