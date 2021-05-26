package com.github.engatec.vdl.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.preferences.wrapper.general.AlwaysAskDownloadPathPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.DownloadPathPref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.worker.UpdateBinariesTask;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class AppUtils {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

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

    public static void updateYoutubeDl(Stage stage, Runnable onSuccessListener) {
        ApplicationContext ctx = ApplicationContext.INSTANCE;
        if (!Files.isWritable(ctx.getYoutubeDlPath())) {
            Dialogs.error("update.youtubedl.nopermissions");
            return;
        }

        String title = ctx.getResourceBundle().getString("dialog.progress.title.label.updateinprogress");
        Dialogs.progress(title, stage, new UpdateBinariesTask(), onSuccessListener);
    }

    public static String convertDtmToString(LocalDateTime dtm) {
        return dtm.format(DATE_TIME_FORMATTER);
    }
}
