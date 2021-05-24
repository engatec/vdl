package com.github.engatec.vdl.ui.stage.subscriptions;

import java.util.List;

import com.github.engatec.vdl.controller.stage.subscriptions.PlaylistContentsController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.stage.AppStage;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PlaylistContentsStage extends AppStage {

    private final String playlistUrl;
    private final List<VideoInfo> videoInfoList;

    public PlaylistContentsStage(String playlistUrl, List<VideoInfo> videoInfoList) {
        this.playlistUrl = playlistUrl;
        this.videoInfoList = videoInfoList;
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
        stage.setOnShown(event -> {
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            boolean stageResized = false;

            double maxScreenWidth = screenBounds.getWidth() / 1.5;
            if (stage.getWidth() > maxScreenWidth) {
                stage.setWidth(maxScreenWidth);
                stageResized = true;
            }

            double maxScreenHeight = screenBounds.getHeight() / 1.5;
            if (stage.getHeight() > maxScreenHeight) {
                stage.setHeight(maxScreenHeight);
                stage.setWidth(stage.getWidth() + 30);
                stageResized = true;
            }

            if (stageResized) {
                stage.centerOnScreen();
            }
        });
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new PlaylistContentsController(stage, playlistUrl, videoInfoList);
    }
}
