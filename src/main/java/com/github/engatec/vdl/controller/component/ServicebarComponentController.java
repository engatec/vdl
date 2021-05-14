package com.github.engatec.vdl.controller.component;

import com.github.engatec.vdl.ui.stage.AboutStage;
import com.github.engatec.vdl.ui.stage.PreferencesStage;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ServicebarComponentController extends HBox {

    private final Stage stage;

    @FXML private ImageView helpButton;
    @FXML private ImageView preferencesButton;

    public ServicebarComponentController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        helpButton.setOnMouseClicked(e -> {
            new AboutStage().modal(stage).showAndWait();
            e.consume();
        });

        preferencesButton.setOnMouseClicked(e -> {
            new PreferencesStage().modal(stage).showAndWait();
            e.consume();
        });
    }
}
