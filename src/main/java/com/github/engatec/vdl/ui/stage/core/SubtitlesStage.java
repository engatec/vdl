package com.github.engatec.vdl.ui.stage.core;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.stage.controller.SubtitlesController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SubtitlesStage extends ResizedOnStartupStage {

    private final VideoInfo videoInfo;

    public SubtitlesStage(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setTitle(ctx.getLocalizedString("subtitles"));
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/search/subtitles.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new SubtitlesController(stage, videoInfo);
    }
}
