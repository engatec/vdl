package com.github.engatec.vdl.ui.stage.subscriptions;

import java.util.List;

import com.github.engatec.vdl.controller.stage.subscriptions.PlaylistContentsController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.stage.AppStage;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PlaylistContentsStage extends AppStage {

    private final List<VideoInfo> contentList;

    public PlaylistContentsStage(List<VideoInfo> contentList) {
        this.contentList = contentList;
        init();
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/subscriptions/playlist_contents.fxml";
    }

    @Override
    protected void init() {
        super.init();
        stage.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("subscriptions.stage.title"));
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new PlaylistContentsController(stage, contentList);
    }
}
