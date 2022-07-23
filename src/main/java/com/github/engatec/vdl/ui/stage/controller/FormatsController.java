package com.github.engatec.vdl.ui.stage.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.youtubedl.YoutubeDlAttr;
import com.github.engatec.vdl.handler.ComboBoxMouseScrollHandler;
import com.github.engatec.vdl.model.Format;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.Video;
import com.github.engatec.vdl.util.LabelUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

public class FormatsController {

    private final ApplicationContext ctx = ApplicationContext.getInstance();

    private Stage stage;
    private VideoInfo videoInfo;
    private String previouslyChosenFormat;
    private Map<String, String> videoExtToAudioExtMap;
    private Consumer<String> onOkButtonClickListener;

    @FXML private GridPane formatsGridPane;
    @FXML private Button okButton;

    private final ToggleGroup toggleGroup = new ToggleGroup();

    public FormatsController() {
    }

    public FormatsController(Stage stage, VideoInfo videoInfo, String previouslyChosenFormat, Consumer<String> onOkButtonClickListener) {
        this.stage = stage;
        this.videoInfo = videoInfo;
        this.previouslyChosenFormat = previouslyChosenFormat;
        this.onOkButtonClickListener = onOkButtonClickListener;
        this.videoExtToAudioExtMap = Map.of("mp4", "m4a", "webm", "webm");
    }

    @FXML
    public void initialize() {
        Pair<List<Video>, List<Audio>> gridData = prepareGridData();
        List<Video> videos = gridData.getLeft();
        List<Audio> audios = gridData.getRight();

        for (Video video : videos) {
            addItemToGrid(video, audios);
        }

        if (toggleGroup.getSelectedToggle() == null) {
            toggleGroup.getToggles().stream().findFirst().ifPresent(toggleGroup::selectToggle);
        }

        okButton.setOnAction(this::handleOkButtonClick);
    }

    private void handleOkButtonClick(ActionEvent e) {
        Toggle selectedRadioButton = toggleGroup.getSelectedToggle();
        if (selectedRadioButton != null) {
            Video video = (Video) selectedRadioButton.getUserData();
            onOkButtonClickListener.accept(video.getFormatId());
        }

        stage.close();
        e.consume();
    }

    private Pair<List<Video>, List<Audio>> prepareGridData() {
        List<Format> formats = videoInfo.formats();
        if (CollectionUtils.isEmpty(formats)) {
            return Pair.of(List.of(), List.of());
        }

        List<Video> videoList = new ArrayList<>();
        List<Audio> audioList = new ArrayList<>();
        final String noCodec = YoutubeDlAttr.NO_CODEC.getValue();

        for (Format format : formats) {
            String acodec = format.getAcodec();
            String vcodec = format.getVcodec();
            if (noCodec.equals(vcodec) && noCodec.equals(acodec)) {
                continue;
            } else if (noCodec.equals(vcodec)) {
                audioList.add(new Audio(format));
            } else if (noCodec.equals(acodec)) {
                videoList.add(new Video(format));
            } else {
                videoList.add(new Video(format, new Audio(format)));
            }
        }

        videoList.sort(
                comparing(Video::getWidth, nullsFirst(naturalOrder()))
                        .thenComparing(Video::getHeight, nullsFirst(naturalOrder()))
                        .thenComparing(Video::getFilesize, nullsFirst(naturalOrder()))
                        .reversed()
        );

        audioList.sort(
                comparing(Audio::getBitrate, nullsFirst(naturalOrder()))
                        .thenComparing(Audio::getFilesize, nullsFirst(naturalOrder()))
                        .reversed()
        );

        return Pair.of(videoList, audioList);
    }

    private void addItemToGrid(Video video, List<Audio> audios) {
        ObservableList<Audio> audioObservableList = FXCollections.observableArrayList(audios);
        Node[] nodes = {
                createRadioButton(video),
                createResolutionLabel(video),
                createSizeLabel(video),
                createCodecLabel(video),
                createExtensionLabel(video),
                createFpsLabel(video),
                video.getAudio() != null || audioObservableList.isEmpty() ? createAudioUnmodifiableComboBox() : createAudioComboBox(video, audioObservableList),
        };
        formatsGridPane.addRow(formatsGridPane.getRowCount(), nodes);
    }

    private RadioButton createRadioButton(Video video) {
        RadioButton radioButton = new RadioButton();
        radioButton.setUserData(video);
        toggleGroup.getToggles().add(radioButton);

        String previouslyChosenVideoFormat = StringUtils.substringBefore(previouslyChosenFormat, Video.VIDEO_AUDIO_FORMAT_SEPARATOR);
        if (StringUtils.equals(previouslyChosenVideoFormat, video.getId())) {
            radioButton.setSelected(true);
        }

        return radioButton;
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

    private Node createFpsLabel(Video video) {
        Label label = new Label();
        label.setText(LabelUtils.formatFps(video.getFps()));
        return label;
    }

    private ComboBox<String> createAudioUnmodifiableComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMaxWidth(Double.MAX_VALUE);
        comboBox.setValue(ctx.getLocalizedString("audio.track.unmodifiable"));
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

                        setText(LabelUtils.formatAudio(item));
                    }
                };
            }
        });

        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Audio item) {
                return item == null ? null : LabelUtils.formatAudio(item);
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

        comboBox.setOnScroll(new ComboBoxMouseScrollHandler());

        SingleSelectionModel<Audio> selectionModel = comboBox.getSelectionModel();
        String matchedAudioExtension = videoExtToAudioExtMap.get(video.getExtension());
        items.stream()
                .filter(it -> StringUtils.equals(matchedAudioExtension, it.getExtension()))
                .findFirst()
                .ifPresentOrElse(selectionModel::select, selectionModel::selectFirst);
        video.setAudio(selectionModel.getSelectedItem());

        return comboBox;
    }
}
