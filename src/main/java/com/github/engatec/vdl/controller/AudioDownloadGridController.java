package com.github.engatec.vdl.controller;

import java.util.List;

import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.util.LabelUtils;
import com.github.engatec.vdl.worker.data.DownloadableData;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.ListUtils;

public class AudioDownloadGridController extends AbstractDownloadGridController {

    private Stage parent;

    @FXML private GridPane rootGridPane;

    @FXML private Label trackNumberLabel;
    @FXML private Label bitrateTitleLabel;
    @FXML private Label sizeTitleLabel;
    @FXML private Label codecTitleLabel;
    @FXML private Label extensionTitleLabel;

    private List<Audio> audioList;
    private List<Postprocessing> postprocessingList;

    private AudioDownloadGridController() {
    }

    public AudioDownloadGridController(Stage parent, DownloadableData downloadableData) {
        super();
        this.parent = parent;
        this.audioList = ListUtils.emptyIfNull(downloadableData.getAudioList());
        this.postprocessingList = downloadableData.getPostprocessingList();
    }

    @FXML
    public void initialize() {
        setLocaleBindings();

        for (Audio audio : audioList) {
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
        return super.createButtonPane(
                super.createDownloadButton(parent, audio, postprocessingList),
                super.createAddToQueueButton(parent, audio, postprocessingList)
        );
    }
}
