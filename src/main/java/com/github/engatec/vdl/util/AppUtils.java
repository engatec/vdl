package com.github.engatec.vdl.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.command.Command;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.preferences.general.AlwaysAskDownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.general.DownloadPathConfigItem;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppUtils {

    public static void executeCommandResolvingPath(Stage stage, Command command, Consumer<Path> onPathResolved) {
        Path path = resolveDownloadPath(stage);
        if (path == null) {
            return;
        }

        boolean pathIsWritableDirectory = Files.isDirectory(path) && Files.isWritable(path);
        if (!pathIsWritableDirectory) {
            Dialogs.error(ApplicationContext.INSTANCE.getResourceBundle().getString("download.path.directory.error"));
            return;
        }

        onPathResolved.accept(path);
        command.execute();
    }

    private static Path resolveDownloadPath(Stage stage) {
        ConfigManager cfg = ConfigManager.INSTANCE;
        Path downloadPath = Paths.get(cfg.getValue(new DownloadPathConfigItem()));
        boolean askPath = cfg.getValue(new AlwaysAskDownloadPathConfigItem());
        if (askPath) {
            var directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            downloadPath = selectedDirectory != null ? selectedDirectory.toPath() : null;
        }

        return downloadPath;
    }
}
