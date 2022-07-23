package com.github.engatec.vdl.service.newversion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.dto.github.ReleaseDto;
import com.github.engatec.vdl.exception.ServiceStubException;
import com.github.engatec.vdl.model.FormattedResource;
import com.github.engatec.vdl.ui.helper.Dialogs;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VdlUpdater implements Updater {

    private static final Logger LOGGER = LogManager.getLogger(VdlUpdater.class);

    private final Stage stage;
    private Runnable onComplete;

    public VdlUpdater(Stage stage) {
        this.stage = stage;
    }

    public void setOnComplete(Runnable handler) {
        this.onComplete = handler;
    }

    @Override
    public void update() {
        var newVersionCheckService = new VdlNewVersionCheckService();

        newVersionCheckService.setOnSucceeded(event -> {
            @SuppressWarnings("unchecked")
            var releaseDtoOptional = (Optional<ReleaseDto>) event.getSource().getValue();
            releaseDtoOptional.ifPresent(it -> Dialogs.infoWithYesNoButtons(
                    new FormattedResource("update.available", "VDL"),
                    () -> downloadNewVersion(it),
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
        if (!Files.isWritable(ApplicationContext.getInstance().getAppBinariesDir())) {
            Dialogs.error(new FormattedResource("update.nopermissions", "vdl"));
            return;
        }

        var newVersionDownloadService = new VdlNewVersionDownloadService(releaseInfo);

        newVersionDownloadService.setOnSucceeded(event -> {
            Dialogs.info(new FormattedResource("update.vdl.finished", releaseInfo.tagName()));
            try {
                String updaterFileName = SystemUtils.IS_OS_WINDOWS ? "updater.cmd" : "updater";
                Path binariesDir = ApplicationContext.getInstance().getAppBinariesDir();
                new ProcessBuilder(binariesDir.resolve(updaterFileName).toString())
                        .directory(binariesDir.toFile())
                        .start();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

        newVersionDownloadService.setOnFailed(event -> {
            Throwable ex = Objects.requireNonNullElseGet(event.getSource().getException(), () -> new ServiceStubException(newVersionDownloadService.getClass()));
            LOGGER.error(ex.getMessage(), ex);
        });

        Dialogs.progress("dialog.progress.title.label.updateinprogress", stage, newVersionDownloadService);
    }
}
