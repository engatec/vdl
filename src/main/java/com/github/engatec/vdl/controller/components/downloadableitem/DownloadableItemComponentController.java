package com.github.engatec.vdl.controller.components.downloadableitem;

import com.github.engatec.vdl.model.VideoInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DownloadableItemComponentController extends VBox {

    private final VideoInfo videoInfo;

    @FXML private Label titleLabel;
    @FXML private Label durationLabel;
    @FXML private ComboBox<String> formatsComboBox;
    @FXML private Button allFormatsButton;

    public DownloadableItemComponentController(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @FXML
    public void initialize() {
        Context ctx = new Context(titleLabel, durationLabel, formatsComboBox, allFormatsButton);
        Initializer.initialize(ctx, videoInfo);
    }
}
