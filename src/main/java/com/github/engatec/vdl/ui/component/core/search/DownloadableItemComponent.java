package com.github.engatec.vdl.ui.component.core.search;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.component.controller.search.DownloadableItemComponentController;
import com.github.engatec.vdl.ui.component.core.AppComponent;
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
