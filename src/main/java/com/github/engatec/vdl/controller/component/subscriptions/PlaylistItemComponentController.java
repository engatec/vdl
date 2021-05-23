package com.github.engatec.vdl.controller.component.subscriptions;

import com.github.engatec.vdl.model.VideoInfo;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;

public class PlaylistItemComponentController extends HBox {

    private final VideoInfo videoInfo;

    @FXML private CheckBox checkbox;
    @FXML private Label label;

    public PlaylistItemComponentController(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
    }

    @FXML
    public void initialize() {
        String text = StringUtils.firstNonBlank(videoInfo.getTitle(), videoInfo.getId(), videoInfo.getBaseUrl());
        label.setText(text);
    }

    public boolean isSelected() {
        return checkbox.isSelected();
    }

    public void setSelected(boolean selected) {
        checkbox.setSelected(selected);
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }
}
