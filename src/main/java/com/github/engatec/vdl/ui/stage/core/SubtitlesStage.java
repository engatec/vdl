package com.github.engatec.vdl.ui.stage.core;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.stage.controller.SubtitlesController;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SubtitlesStage extends AppStage {

    private final VideoInfo videoInfo;

    public SubtitlesStage(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setResizable(false);
        stage.setTitle(ctx.getLocalizedString("subtitles"));
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
    protected String getFxmlPath() {
        return "/fxml/search/subtitles.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new SubtitlesController(stage, videoInfo);
    }
}
