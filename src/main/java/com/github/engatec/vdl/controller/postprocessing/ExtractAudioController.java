package com.github.engatec.vdl.controller.postprocessing;

import java.util.Locale;
import java.util.function.Consumer;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.postprocessing.ExtractAudioPostprocessing;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.ui.Icons;
import com.github.engatec.vdl.util.Svg;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

public class ExtractAudioController extends StageAwareController {

    private static final int DEFAULT_QUALITY_VALUE = 5;

    private ExtractAudioPostprocessing model;
    private Consumer<? super Postprocessing> okClickCallback;

    @FXML private GridPane contentGridPane;

    @FXML private ComboBox<Format> formatComboBox;
    @FXML private Slider qualitySlider;

    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private enum Format {
        MP3, AAC, M4A, FLAC, OPUS, VORBIS, WAV;

        public static Format getByString(String value) {
            if (StringUtils.isBlank(value)) {
                return MP3;
            }

            for (Format format : values()) {
                if (value.equalsIgnoreCase(format.name())) {
                    return format;
                }
            }
            return MP3;
        }

        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

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

        formatComboBox.getItems().addAll(Format.values());
        formatComboBox.getSelectionModel().selectFirst();

        qualitySlider.setValue(DEFAULT_QUALITY_VALUE);

        applyModel();

        okButton.setOnAction(this::handleOkButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private void addInfoIcon() {
        Group infoIcon = Icons.info();
        Svg.scale(infoIcon, 0.8);
        Tooltip tooltip = new Tooltip(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.postprocessing.extractaudio.quality.tooltip"));
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setShowDuration(Duration.INDEFINITE);
        Tooltip.install(infoIcon, tooltip);
        contentGridPane.add(infoIcon, 2, 1);
    }

    private void applyModel() {
        if (model == null) {
            return;
        }

        formatComboBox.getSelectionModel().select(Format.getByString(model.getFormat()));
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
