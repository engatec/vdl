package com.github.engatec.vdl.ui.controller;

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
