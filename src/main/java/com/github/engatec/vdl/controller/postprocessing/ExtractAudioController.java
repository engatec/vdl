package com.github.engatec.vdl.controller.postprocessing;

import java.util.function.Consumer;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.model.AudioFormat;
import com.github.engatec.vdl.model.postprocessing.ExtractAudioPostprocessing;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.ui.SvgIcons;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ExtractAudioController extends StageAwareController {

    private static final int DEFAULT_QUALITY_VALUE = 5;

    private ExtractAudioPostprocessing model;
    private Consumer<? super Postprocessing> okClickCallback;

    @FXML private GridPane contentGridPane;

    @FXML private ComboBox<AudioFormat> formatComboBox;
    @FXML private Slider qualitySlider;

    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private ExtractAudioController() {
    }

    public ExtractAudioController(Stage stage, ExtractAudioPostprocessing model, Consumer<? super Postprocessing> okClickCallback) {
        super(stage);
        this.model = model;
        this.okClickCallback = okClickCallback;
    }

    @FXML
    public void initialize() {
        addInfoIcon();

        formatComboBox.getItems().addAll(AudioFormat.values());
        formatComboBox.getSelectionModel().selectFirst();

        qualitySlider.setValue(DEFAULT_QUALITY_VALUE);

        applyModel();

        okButton.setOnAction(this::handleOkButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private void addInfoIcon() {
        contentGridPane.add(SvgIcons.infoWithTooltip("stage.postprocessing.extractaudio.quality.tooltip"), 2, 1);
    }

    private void applyModel() {
        if (model == null) {
            return;
        }

        formatComboBox.getSelectionModel().select(AudioFormat.getByString(model.getFormat()));
        qualitySlider.setValue(model.getQuality());
    }

    private void handleOkButtonClick(ActionEvent event) {
        okClickCallback.accept(ExtractAudioPostprocessing.newInstance(formatComboBox.getValue().toString(), (int) qualitySlider.getValue()));
        stage.close();
        event.consume();
    }

    private void handleCancelButtonClick(ActionEvent e) {
        stage.close();
        e.consume();
    }
}
