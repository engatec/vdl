package com.github.engatec.vdl.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.ConfigProperty;
import com.github.engatec.vdl.model.Downloadable;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class AbstractDownloadGridController extends GridPane {

    protected Button createDownloadButton(Stage parentStage, Downloadable downloadable) {
        Button downloadBtn = new Button();
        I18n.bindLocaleProperty(downloadBtn.textProperty(), "download");
        downloadBtn.setOnAction(event -> {
            Path downloadPath = getDownloadPath(parentStage);
            if (downloadPath == null) {
                return;
            }

            boolean pathIsWritableDirectory = Files.isDirectory(downloadPath) && Files.isWritable(downloadPath);
            if (!pathIsWritableDirectory) {
                Dialogs.error(ApplicationContext.INSTANCE.getResourceBundle().getString("download.path.directory.error"));
                return;
            }

            Stage stage = UiManager.loadStage(UiComponent.DOWNLOADING_PROGRESS, new Stage(), param -> new DownloadingProgressController(downloadable, downloadPath));
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(parentStage);
            stage.showAndWait();
            event.consume();
        });
        return downloadBtn;
    }

    protected Path getDownloadPath(Stage parentStage) {
        Path downloadPath = Paths.get(ConfigManager.INSTANCE.getValue(ConfigProperty.DOWNLOAD_PATH));
        boolean askPath = Boolean.parseBoolean(ConfigManager.INSTANCE.getValue(ConfigProperty.DOWNLOAD_ALWAYS_ASK_PATH));
        if (askPath) {
            var directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(parentStage);
            downloadPath = selectedDirectory != null ? selectedDirectory.toPath() : null;
        }

        return downloadPath;
    }
}
