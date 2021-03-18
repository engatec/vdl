package com.github.engatec.vdl.ui.stage.postprocessing;

import com.github.engatec.vdl.controller.postprocessing.PostprocessingController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.ui.stage.AppStage;
import javafx.stage.Stage;
import javafx.util.Callback;

public class PostprocessingStage extends AppStage {

    private final Downloadable downloadable;

    public PostprocessingStage(Downloadable downloadable) {
        this.downloadable = downloadable;
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setTitle(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.postprocessing.title"));
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/postprocessing/postprocessing.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new PostprocessingController(stage, downloadable);
    }
}
