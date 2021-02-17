package com.github.engatec.vdl.ui;

import java.util.function.Function;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Stages {

    public static Stage newModalStage(UiComponent component, Function<Stage, StageAwareController> controllerCreator, Stage owner) {
        Stage stage = new Stage();
        UiManager.loadStage(component, stage, param -> controllerCreator.apply(stage));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        return stage;
    }
}
