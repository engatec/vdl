package com.github.engatec.vdl.ui.stage.core;

import java.util.function.Consumer;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.stage.controller.FormatsController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class FormatsStage extends ResizedOnStartupStage {

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
        stage.setTitle(ctx.getLocalizedString("format.select"));
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
