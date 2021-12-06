package com.github.engatec.vdl.ui.stage;

import java.util.List;
import java.util.function.Consumer;

import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.controller.stage.PlaylistContentsController;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PlaylistContentsStage extends AppStage {

    private final String playlistUrl;
    private final List<VideoInfo> videoInfoList;
    private final Consumer<Subscription> onSubscribeListener;

    public PlaylistContentsStage(String playlistUrl, List<VideoInfo> videoInfoList, Consumer<Subscription> onSubscribeListener) {
        this.playlistUrl = playlistUrl;
        this.videoInfoList = videoInfoList;
        this.onSubscribeListener = onSubscribeListener;
        init();
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/subscriptions/playlist_contents.fxml";
    }

    @Override
    protected void init() {
        super.init();
        stage.setTitle(ctx.getLocalizedString("subscriptions.stage.title"));
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
        return param -> new PlaylistContentsController(stage, playlistUrl, videoInfoList, onSubscribeListener);
    }
}
