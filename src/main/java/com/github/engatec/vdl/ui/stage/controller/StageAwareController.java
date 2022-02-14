package com.github.engatec.vdl.ui.stage.controller;

import javafx.stage.Stage;

public abstract class StageAwareController {

    protected Stage stage;

    protected StageAwareController() {
        throw new UnsupportedOperationException();
    }

    public StageAwareController(Stage stage) {
        this.stage = stage;
    }
}
