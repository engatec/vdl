package com.github.engatec.vdl.stage;

import com.github.engatec.vdl.controller.preferences.PostprocessingController;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PostprocessingStage extends AppStage {

    private final Downloadable downloadable;

    public PostprocessingStage(Downloadable downloadable) {
        this.downloadable = downloadable;
        load();
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/postprocessing.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new PostprocessingController(stage, downloadable);
    }
}
