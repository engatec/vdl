package com.github.engatec.vdl.ui.stage.core;

import java.util.List;
import java.util.function.Consumer;

import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.stage.controller.PlaylistContentsController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PlaylistContentsStage extends ResizedOnStartupStage {

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
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new PlaylistContentsController(stage, playlistUrl, videoInfoList, onSubscribeListener);
    }
}
