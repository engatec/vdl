package com.github.engatec.vdl.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.engatec.vdl.core.command.Command;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppUtils {

    public static void executeCommandResolvingPath(Stage stage, Command command, Consumer<Path> onPathResolved) {
        resolveDownloadPath(stage).ifPresent(path -> {
            onPathResolved.accept(path);
            command.execute();
        });
    }

    public static Optional<Path> resolveDownloadPath(Stage stage) {
        Path path = doResolveDownloadPath(stage);
        if (path == null) {
            return Optional.empty();
        }

        boolean pathIsWritableDirectory = Files.isDirectory(path) && Files.isWritable(path);
        if (!pathIsWritableDirectory) {
            Dialogs.error("download.path.directory.error");
            return Optional.empty();
        }

        return Optional.of(path);
    }

    private static Path doResolveDownloadPath(Stage stage) {
        Path downloadPath = Paths.get(ConfigRegistry.get(DownloadPathPref.class).getValue());
        boolean askPath = ConfigRegistry.get(AlwaysAskDownloadPathPref.class).getValue();
        if (askPath) {
            var directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            downloadPath = selectedDirectory != null ? selectedDirectory.toPath() : null;
        }

        return downloadPath;
    }
}
