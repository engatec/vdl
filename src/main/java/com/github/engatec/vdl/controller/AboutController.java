package com.github.engatec.vdl.controller;

import java.util.ResourceBundle;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.YoutubeDlManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class AboutController extends StageAwareController {

    private static final String UNKNOWN_VERSION = "unknown";

    @FXML private Label vdlVersionLabel;
    @FXML private Label youtubeDlVersionLabel;
    @FXML private Button closeBtn;

    private AboutController() {
    }

    public AboutController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        ResourceBundle resBundle = ApplicationContext.INSTANCE.getResourceBundle();
        stage.setTitle(resBundle.getString("stage.about.title"));
        vdlVersionLabel.setText(String.format(resBundle.getString("stage.about.label.version.vdl"), getVdlVersion()));
        youtubeDlVersionLabel.setText(String.format(resBundle.getString("stage.about.label.version.youtubedl"), getYoutubeDlVersion()));
        closeBtn.setOnAction(this::handleCloseButton);
    }

    private String getVdlVersion() {
        return StringUtils.defaultIfBlank(getClass().getPackage().getImplementationVersion(), UNKNOWN_VERSION);
    }

    private String getYoutubeDlVersion() {
        return StringUtils.defaultIfBlank(YoutubeDlManager.INSTANCE.getCurrentYoutubeDlVersion(), UNKNOWN_VERSION);
    }

    private void handleCloseButton(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
