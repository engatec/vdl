package com.github.engatec.vdl.stage.postprocessing;

import java.util.function.Consumer;

import com.github.engatec.vdl.controller.postprocessing.ExtractAudioController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.postprocessing.ExtractAudioPostprocessing;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.stage.AppStage;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ExtractAudioStage extends AppStage {

    private final ExtractAudioPostprocessing model;
    private final Consumer<? super Postprocessing> okClickCallback;

    public ExtractAudioStage(ExtractAudioPostprocessing model, Consumer<? super Postprocessing> okClickCallback) {
        this.model = model;
        this.okClickCallback = okClickCallback;
        stage.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.postprocessing.extractaudio"));
        stage.setResizable(false);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/postprocessing/extract-audio.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new ExtractAudioController(stage, model, okClickCallback);
    }
}
