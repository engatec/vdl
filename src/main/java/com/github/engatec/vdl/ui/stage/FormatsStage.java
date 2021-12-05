package com.github.engatec.vdl.ui.stage;

import java.util.function.Consumer;

import com.github.engatec.vdl.controller.stage.FormatsController;
import com.github.engatec.vdl.model.VideoInfo;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FormatsStage extends AppStage {

    private final VideoInfo videoInfo;
    private final String previouslyChosenFormat;
    private final Consumer<String> onOkButtonClickListener;

    public FormatsStage(VideoInfo videoInfo, String previouslyChosenFormat, Consumer<String> onOkButtonClickListener) {
        this.videoInfo = videoInfo;
        this.previouslyChosenFormat = previouslyChosenFormat;
        this.onOkButtonClickListener = onOkButtonClickListener;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setResizable(false);
        stage.setTitle(ctx.getLocalizedString("format.select"));
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
        return "/fxml/search/formats.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new FormatsController(stage, videoInfo, previouslyChosenFormat, onOkButtonClickListener);
    }
}
