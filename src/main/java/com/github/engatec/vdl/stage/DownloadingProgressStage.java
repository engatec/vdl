package com.github.engatec.vdl.stage;

import com.github.engatec.vdl.controller.DownloadingProgressController;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import javafx.stage.Stage;
import javafx.util.Callback;

public class DownloadingProgressStage extends AppStage {

    private final Downloadable downloadable;

    public DownloadingProgressStage(Downloadable downloadable) {
        this.downloadable = downloadable;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setResizable(false);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/downloading-progress.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new DownloadingProgressController(stage, downloadable);
    }
}
