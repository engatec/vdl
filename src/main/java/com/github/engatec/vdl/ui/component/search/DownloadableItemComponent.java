package com.github.engatec.vdl.ui.component.search;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.component.AppComponent;
import com.github.engatec.vdl.ui.controller.component.search.DownloadableItemComponentController;
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
        return new DownloadableItemComponentController(stage, videoInfo);
    }
}
