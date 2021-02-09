package com.github.engatec.vdl.controller;

import java.io.File;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.ConfigManager;
import com.github.engatec.vdl.core.ConfigProperty;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class PreferencesController implements StageAware {

    private Stage stage;

    private final ToggleGroup downloadRadioGroup = new ToggleGroup();

    @FXML private RadioButton downloadPathRadioBtn;
    @FXML private RadioButton askPathRadioBtn;
    @FXML private TextField downloadPathTextField;
    @FXML private Button chooseDownloadPathBtn;

    @FXML private Button okBtn;
    @FXML private Button cancelBtn;

    @FXML
    public void initialize() {
        okBtn.setOnAction(this::handleOkBtnClick);
        cancelBtn.setOnAction(this::handleCancelBtnClick);
        chooseDownloadPathBtn.setOnAction(this::handleDownloadPathChoose);

        initDownloadRadioGroup();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
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

    private void handleCancelBtnClick(ActionEvent event) {
        stage.close();
        event.consume();
    }

    private void handleOkBtnClick(ActionEvent event) {
        ConfigManager config = ConfigManager.INSTANCE;
        Map<ConfigProperty, String> initialConfig = Arrays.stream(ConfigProperty.values()).collect(Collectors.toMap(Function.identity(), config::getValue));
        config.setValue(ConfigProperty.DOWNLOAD_ALWAYS_ASK_PATH, String.valueOf(askPathRadioBtn.isSelected()));
        config.setValue(ConfigProperty.DOWNLOAD_PATH, downloadPathTextField.getText());
        try {
            config.saveConfig();
        } catch (UncheckedIOException e) {
            Dialogs.error(ApplicationContext.INSTANCE.getResourceBundle().getString("preferences.save.error"));
            for (var entry : initialConfig.entrySet()) {
                config.setValue(entry.getKey(), entry.getValue());
            }
        }
        stage.close();
        event.consume();
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
