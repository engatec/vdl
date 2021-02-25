package com.github.engatec.vdl.controller.preferences;

import com.github.engatec.vdl.core.preferences.propertyholder.PostprocessingPropertyHolder;
import javafx.fxml.FXML;

public class PostprocessingPreferencesController {


    private PostprocessingPropertyHolder propertyHolder;

    private PostprocessingPreferencesController() {
    }

    public PostprocessingPreferencesController(PostprocessingPropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    @FXML
    public void initialize() {

        bindPropertyHolder();
    }

    private void bindPropertyHolder() {
    }
}
