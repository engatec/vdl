package com.github.engatec.vdl.core.action;

import com.github.engatec.vdl.controller.DownloadingProgressController;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.ui.Stages;
import javafx.stage.Stage;

public class DownloadAction implements Action {

    private final Stage parentStage;
    private final Downloadable downloadable;

    public DownloadAction(Stage parentStage, Downloadable downloadable) {
        this.parentStage = parentStage;
        this.downloadable = downloadable;
    }

    @Override
    public void perform() {
        Stage stage = Stages.newModalStage(UiComponent.DOWNLOADING_PROGRESS, s -> new DownloadingProgressController(s, downloadable), parentStage);
        stage.setResizable(false);
        stage.showAndWait();
    }
}
