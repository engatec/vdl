package com.github.engatec.vdl.controller;

import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.command.Command;
import com.github.engatec.vdl.core.command.DownloadCommand;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.util.LabelUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AudioDownloadGridController extends AbstractDownloadGridController {

    private Stage parent;

    @FXML private GridPane rootGridPane;

    @FXML private Label trackNumberLabel;
    @FXML private Label bitrateTitleLabel;
    @FXML private Label sizeTitleLabel;
    @FXML private Label codecTitleLabel;
    @FXML private Label extensionTitleLabel;

    private MultiFormatDownloadable downloadable;

    private AudioDownloadGridController() {
    }

    public AudioDownloadGridController(Stage parent, MultiFormatDownloadable downloadable) {
        super();
        this.parent = parent;
        this.downloadable = downloadable;
    }

    @FXML
    public void initialize() {
        setLocaleBindings();

        for (Audio audio : downloadable.getAudios()) {
            addAudioToGrid(audio);
        }
    }

    private void setLocaleBindings() {
        I18n.bindLocaleProperty(trackNumberLabel.textProperty(), "audio.track");
        I18n.bindLocaleProperty(bitrateTitleLabel.textProperty(), "bitrate");
        I18n.bindLocaleProperty(sizeTitleLabel.textProperty(), "size");
        I18n.bindLocaleProperty(codecTitleLabel.textProperty(), "codec");
        I18n.bindLocaleProperty(extensionTitleLabel.textProperty(), "format");
    }

    private void addAudioToGrid(Audio audio) {
        Node[] nodes = {
                createNumberLabel(audio),
                createBitrateLabel(audio),
                createSizeLabel(audio),
                createCodecLabel(audio),
                createExtensionLabel(audio),
                createButtonPane(audio)
        };
        rootGridPane.addRow(rootGridPane.getRowCount(), nodes);
    }

    private Label createNumberLabel(Audio audio) {
        Label label = new Label();
        label.setText(LabelUtils.formatTrackNo(audio.getTrackNo()));
        return label;
    }

    private Label createBitrateLabel(Audio audio) {
        Label label = new Label();
        label.setText(LabelUtils.formatBitrate(audio.getBitrate()));
        return label;
    }

    private Label createSizeLabel(Audio audio) {
        Label label = new Label();
        label.setText(LabelUtils.formatSize(audio.getFilesize()));
        return label;
    }

    private Label createCodecLabel(Audio audio) {
        Label label = new Label();
        label.setText(LabelUtils.formatCodec(audio.getCodec()));
        return label;
    }

    private Label createExtensionLabel(Audio audio) {
        Label label = new Label();
        label.setText(audio.getExtension());
        return label;
    }

    private HBox createButtonPane(Audio audio) {
        Button downloadBtn = super.createDownloadButton();
        setButtonOnAction(downloadBtn, audio, new DownloadCommand(parent, downloadable));

        Button addToQueueBtn = super.createAddToQueueButton();
        setButtonOnAction(addToQueueBtn, audio, new EnqueueCommand(downloadable));

        return super.createButtonPane(downloadBtn, addToQueueBtn);
    }

    private void setButtonOnAction(Button button, Audio audio, Command command) {
        button.setOnAction(e -> {
            downloadable.setFormatId(audio.getFormatId());
            AppUtils.executeCommandResolvingPath(parent, command, downloadable::setDownloadPath);
            e.consume();
        });
    }
}
