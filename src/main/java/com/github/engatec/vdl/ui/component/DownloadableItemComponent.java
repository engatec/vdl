package com.github.engatec.vdl.ui.component;

import com.github.engatec.vdl.controller.component.DownloadableItemComponentController;
import com.github.engatec.vdl.model.VideoInfo;
import javafx.stage.Stage;

public class DownloadableItemComponent extends AppComponent<DownloadableItemComponentController> {

    private final VideoInfo videoInfo;

    public DownloadableItemComponent(Stage stage, VideoInfo videoInfo) {
        super(stage);
        this.videoInfo = videoInfo;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/search/downloadable-item.fxml";
    }

    @Override
    protected DownloadableItemComponentController getController() {
        return new DownloadableItemComponentController(videoInfo);
    }
}
