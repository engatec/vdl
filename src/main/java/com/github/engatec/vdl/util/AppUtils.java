package com.github.engatec.vdl.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.preferences.general.AlwaysAskDownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.general.DownloadPathConfigItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppUtils {

    public static Optional<Path> resolveDownloadPath(Stage stage) {
        ConfigManager cfg = ConfigManager.INSTANCE;
        Path path = Paths.get(cfg.getValue(new DownloadPathConfigItem()));
        boolean askPath = cfg.getValue(new AlwaysAskDownloadPathConfigItem());
        if (askPath) {
            var directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            path = selectedDirectory != null ? selectedDirectory.toPath() : null;
        }

        if (path != null) {
            boolean pathIsWritableDirectory = Files.isDirectory(path) && Files.isWritable(path);
            if (!pathIsWritableDirectory) {
                path = null;
            }
        }

        return Optional.ofNullable(path);
    }
}
