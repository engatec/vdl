package com.github.engatec.vdl.ui.stage;

import java.util.function.Consumer;

import com.github.engatec.vdl.ui.controller.YoutubeCookiesGeneratorController;
import javafx.stage.Stage;
import javafx.util.Callback;

public class YoutubeCookiesGeneratorStage extends AppStage {

    private final Consumer<String> onOkButtonClickCallback;

    public YoutubeCookiesGeneratorStage(Consumer<String> onOkButtonClickCallback) {
        this.onOkButtonClickCallback = onOkButtonClickCallback;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setMinWidth(700);
        stage.setWidth(1024);
        stage.setMinHeight(400);
        stage.setHeight(768);
        stage.setTitle("YouTube");
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/cookies/youtube.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new YoutubeCookiesGeneratorController(stage, onOkButtonClickCallback);
    }
}
