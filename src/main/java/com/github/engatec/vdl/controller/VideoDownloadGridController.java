package com.github.engatec.vdl.controller;

import java.util.Map;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.command.Command;
import com.github.engatec.vdl.core.command.DownloadCommand;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.Video;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.util.LabelUtils;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

public class VideoDownloadGridController extends AbstractDownloadGridController {

    private Stage parent;

    @FXML private GridPane rootGridPane;

    @FXML private Label resolutionTitleLabel;
    @FXML private Label sizeTitleLabel;
    @FXML private Label codecTitleLabel;
    @FXML private Label extensionTitleLabel;
    @FXML private Label audioTitleLabel;

    private MultiFormatDownloadable downloadable;

    private Map<String, String> videoExtToAudioExtMap;

    private VideoDownloadGridController() {
    }

    public VideoDownloadGridController(Stage parent, MultiFormatDownloadable downloadable) {
        super();
        this.parent = parent;
        this.downloadable = downloadable;

        videoExtToAudioExtMap = Map.of("mp4", "m4a", "webm", "webm");
    }

    @FXML
    public void initialize() {
        setLocaleBindings();

        for (Video video : downloadable.getVideos()) {
            addVideoToGrid(video);
        }
    }

    private void setLocaleBindings() {
        I18n.bindLocaleProperty(resolutionTitleLabel.textProperty(), "resolution");
        I18n.bindLocaleProperty(sizeTitleLabel.textProperty(), "size");
        I18n.bindLocaleProperty(codecTitleLabel.textProperty(), "codec");
        I18n.bindLocaleProperty(extensionTitleLabel.textProperty(), "format");
        I18n.bindLocaleProperty(audioTitleLabel.textProperty(), "audio.track");
    }

    private void addVideoToGrid(Video video) {
        ObservableList<Audio> audioObservableList = FXCollections.observableArrayList(downloadable.getAudios());
        Node[] nodes = {
                createResolutionLabel(video),
                createSizeLabel(video),
                createCodecLabel(video),
                createExtensionLabel(video),
                video.getAudio() != null || audioObservableList.isEmpty() ? createAudioUnmodifiableComboBox() : createAudioComboBox(video, audioObservableList),
                createButtonPane(video)
        };
        rootGridPane.addRow(rootGridPane.getRowCount(), nodes);
    }

    private Label createResolutionLabel(Video video) {
        Label label = new Label();
        label.setText(LabelUtils.formatResolution(video.getWidth(), video.getHeight()));
        return label;
    }

    private Label createSizeLabel(Video video) {
        Label label = new Label();
        label.setText(LabelUtils.formatSize(video.getFilesize()));
        return label;
    }

    private Label createCodecLabel(Video video) {
        Label label = new Label();
        label.setText(LabelUtils.formatCodec(video.getCodec()));
        return label;
    }

    private Label createExtensionLabel(Video video) {
        Label label = new Label();
        label.setText(video.getExtension());
        return label;
    }

    private ComboBox<String> createAudioUnmodifiableComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        ApplicationContext ctx = ApplicationContext.INSTANCE;
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.valueProperty().bind(Bindings.createObjectBinding(() -> ctx.getResourceBundle().getString("audio.track.unmodifiable"), ctx.getResourceBundleProperty()));
        comboBox.setDisable(true);
        return comboBox;
    }

    private ComboBox<Audio> createAudioComboBox(Video video, ObservableList<Audio> items) {
        ComboBox<Audio> comboBox = new ComboBox<>(items);
        comboBox.setMaxWidth(Double.MAX_VALUE);

        comboBox.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Audio> call(ListView<Audio> param) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(Audio item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText(null);
                            return;
                        }

                        setText(LabelUtils.formatTrackNo(item.getTrackNo()));
                    }
                };
            }
        });

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Audio item) {
                return item == null ? null : LabelUtils.formatTrackNo(item.getTrackNo());
            }

            @Override
            public Audio fromString(String string) {
                return null;
            }
        });

        comboBox.setOnAction(event -> {
            Audio audio = ((ComboBox<Audio>) event.getTarget()).getValue();
            video.setAudio(audio);
            event.consume();
        });

        SingleSelectionModel<Audio> selectionModel = comboBox.getSelectionModel();
        String matchedAudioExtension = videoExtToAudioExtMap.get(video.getExtension());
        items.stream()
                .filter(it -> StringUtils.equals(matchedAudioExtension, it.getExtension()))
                .findFirst()
                .ifPresentOrElse(selectionModel::select, selectionModel::selectFirst);
        video.setAudio(selectionModel.getSelectedItem());

        return comboBox;
    }

    private HBox createButtonPane(Video video) {
        Button downloadBtn = super.createDownloadButton();
        setButtonOnAction(downloadBtn, video, new DownloadCommand(parent, downloadable));

        Button addToQueueBtn = super.createAddToQueueButton();
        setButtonOnAction(addToQueueBtn, video, new EnqueueCommand(downloadable));

        return super.createButtonPane(downloadBtn, addToQueueBtn);
    }

    private void setButtonOnAction(Button button, Video video, Command command) {
        button.setOnAction(e -> {
            downloadable.setFormatId(video.getFormatId());
            AppUtils.executeCommandResolvingPath(parent, command, downloadable::setDownloadPath);
            e.consume();
        });
    }
}
