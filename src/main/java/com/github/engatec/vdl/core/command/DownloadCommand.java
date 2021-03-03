package com.github.engatec.vdl.core.command;

import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.stage.DownloadingProgressStage;
import javafx.stage.Stage;

public class DownloadCommand implements Command {

    private final Stage parentStage;
    private final Downloadable downloadable;

    public DownloadCommand(Stage parentStage, Downloadable downloadable) {
        this.parentStage = parentStage;
        this.downloadable = downloadable;
    }

    @Override
    public void execute() {
        new DownloadingProgressStage(downloadable).modal(parentStage).showAndWait();
    }
}
