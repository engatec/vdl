package com.github.engatec.vdl.core.action;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.github.engatec.vdl.controller.DownloadingProgressController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.Downloadable;
import com.github.engatec.vdl.model.preferences.general.AlwaysAskDownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.general.DownloadPathConfigItem;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.Stages;
import javafx.stage.DirectoryChooser;
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

        Stage stage = Stages.newModalStage(UiComponent.DOWNLOADING_PROGRESS, s -> new DownloadingProgressController(s, downloadable, downloadPath), parentStage);
        stage.setResizable(false);
        stage.showAndWait();
    }

    private Path getDownloadPath(Stage parentStage) {
        ConfigManager cfg = ConfigManager.INSTANCE;
        Path downloadPath = Paths.get(cfg.getValue(new DownloadPathConfigItem()));
        boolean askPath = cfg.getValue(new AlwaysAskDownloadPathConfigItem());
        if (askPath) {
            var directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(parentStage);
            downloadPath = selectedDirectory != null ? selectedDirectory.toPath() : null;
        }

        return downloadPath;
    }
}
