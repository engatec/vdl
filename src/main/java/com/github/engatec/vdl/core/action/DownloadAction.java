package com.github.engatec.vdl.core.action;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.engatec.vdl.controller.DownloadingProgressController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.ConfigProperty;
import com.github.engatec.vdl.model.Downloadable;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class DownloadAction {

    private final Stage parentStage;
    private final Downloadable downloadable;

    public DownloadAction(Stage parentStage, Downloadable downloadable) {
        this.parentStage = parentStage;
        this.downloadable = downloadable;
    }

    public void perform() {
        Path downloadPath = getDownloadPath(parentStage);
        if (downloadPath == null) {
            return;
        }

        boolean pathIsWritableDirectory = Files.isDirectory(downloadPath) && Files.isWritable(downloadPath);
        if (!pathIsWritableDirectory) {
            Dialogs.error(ApplicationContext.INSTANCE.getResourceBundle().getString("download.path.directory.error"));
            return;
        }

        Stage stage = new Stage();
        UiManager.loadStage(UiComponent.DOWNLOADING_PROGRESS, stage, param -> new DownloadingProgressController(stage, downloadable, downloadPath));
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(parentStage);
        stage.showAndWait();
    }

    private Path getDownloadPath(Stage parentStage) {
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
