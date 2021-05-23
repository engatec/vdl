package com.github.engatec.vdl.ui.component.subscriptions;

import com.github.engatec.vdl.controller.component.subscriptions.PlaylistItemComponentController;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.component.AppComponent;
import javafx.stage.Stage;

public class PlaylistItemComponent extends AppComponent<PlaylistItemComponentController> {

    private final VideoInfo videoInfo;

    public PlaylistItemComponent(Stage stage, VideoInfo videoInfo) {
        super(stage);
        this.videoInfo = videoInfo;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/subscriptions/playlist_item.fxml";
    }

    @Override
    protected PlaylistItemComponentController getController() {
        return new PlaylistItemComponentController(videoInfo);
    }
}
