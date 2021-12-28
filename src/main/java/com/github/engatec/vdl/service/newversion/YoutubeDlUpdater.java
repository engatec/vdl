package com.github.engatec.vdl.service.newversion;

import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.exception.ServiceStubException;
import com.github.engatec.vdl.model.FormattedResource;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YoutubeDlUpdater implements Updater {

    private static final Logger LOGGER = LogManager.getLogger(YoutubeDlUpdater.class);

    private final Stage stage;
    private Consumer<String> onSucceeded;
    private Runnable onComplete;

    public YoutubeDlUpdater(Stage stage) {
        this.stage = stage;
    }

    public void setOnSucceeded(Consumer<String> handler) {
        this.onSucceeded = handler;
    }

    public void setOnComplete(Runnable handler) {
        this.onComplete = handler;
    }

    @Override
    public void update() {
        var newVersionCheckService = new YoutubeDlNewVersionCheckService();

        newVersionCheckService.setOnSucceeded(event -> {
            @SuppressWarnings("unchecked")
            var releaseDtoOptional = (Optional<ReleaseDto>) event.getSource().getValue();
            releaseDtoOptional.ifPresent(releaseInfo -> Dialogs.infoWithYesNoButtons(
                    new FormattedResource("update.available", "youtube-dl"),
                    () -> downloadNewVersion(releaseInfo),
                    null
            ));
        });

        newVersionCheckService.setOnFailed(event -> {
            Throwable ex = Objects.requireNonNullElseGet(event.getSource().getException(), () -> new ServiceStubException(newVersionCheckService.getClass()));
            LOGGER.error(ex.getMessage(), ex);
        });

        if (onComplete != null) {
            newVersionCheckService.runningProperty().addListener((observable, oldValue, newValue) -> {
                if (Boolean.TRUE.equals(oldValue) && Boolean.FALSE.equals(newValue)) {
                    onComplete.run();
                }
            });
        }

        newVersionCheckService.start();
    }

    private void downloadNewVersion(ReleaseDto releaseInfo) {
        if (!Files.isWritable(ApplicationContext.getInstance().getDownloaderPath(Engine.YOUTUBE_DL))) {
            Dialogs.error(new FormattedResource("update.nopermissions", "youtube-dl"));
            return;
        }

        var newVersionDownloadService = new YoutubeDlNewVersionDownloadService();

        if (onSucceeded != null) {
            newVersionDownloadService.setOnSucceeded(event -> onSucceeded.accept(releaseInfo.tagName()));
        }

        newVersionDownloadService.setOnFailed(event -> {
            Throwable ex = Objects.requireNonNullElseGet(event.getSource().getException(), () -> new ServiceStubException(newVersionDownloadService.getClass()));
            LOGGER.error(ex.getMessage(), ex);
        });

        Dialogs.progress("dialog.progress.title.label.updateinprogress", stage, newVersionDownloadService);
    }
}
