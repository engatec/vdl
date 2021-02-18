package com.github.engatec.vdl.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.action.Action;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.preferences.general.AlwaysAskDownloadPathConfigItem;
import com.github.engatec.vdl.model.preferences.general.DownloadPathConfigItem;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ActionUtils {

    public static void performActionResolvingPath(Stage parentStage, Action action, Consumer<Path> onPathResolved) {
        Path path = resolveDownloadPath(parentStage);
        if (path == null) {
            return;
        }

        boolean pathIsWritableDirectory = Files.isDirectory(path) && Files.isWritable(path);
        if (!pathIsWritableDirectory) {
            Dialogs.error(ApplicationContext.INSTANCE.getResourceBundle().getString("download.path.directory.error"));
            return;
        }

        onPathResolved.accept(path);
        action.perform();
    }

    private static Path resolveDownloadPath(Stage parentStage) {
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
