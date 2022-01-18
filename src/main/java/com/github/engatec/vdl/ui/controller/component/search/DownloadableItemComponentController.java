package com.github.engatec.vdl.ui.controller.component.search;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.AudioFormat;
import com.github.engatec.vdl.model.Format;
import com.github.engatec.vdl.model.QueueItem;
import com.github.engatec.vdl.model.Resolution;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.ExtractAudioPostprocessing;
import com.github.engatec.vdl.model.preferences.wrapper.general.AudioExtractionFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AudioExtractionQualityPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoSelectFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.LoadThumbnailsPref;
import com.github.engatec.vdl.model.preferences.wrapper.misc.RecentDownloadPathPref;
import com.github.engatec.vdl.ui.Icon;
import com.github.engatec.vdl.ui.Tooltips;
import com.github.engatec.vdl.ui.data.ComboBoxValueHolder;
import com.github.engatec.vdl.ui.stage.FormatsStage;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.util.YouDlUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadableItemComponentController extends HBox {

    private static final Logger LOGGER = LogManager.getLogger(DownloadableItemComponentController.class);

    private static final String CUSTOM_FORMAT_LABEL = "Custom format";

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final QueueManager queueManager = ctx.getManager(QueueManager.class);
    private final HistoryManager historyManager = ctx.getManager(HistoryManager.class);

    private final Stage stage;
    private final VideoInfo videoInfo;
    private String customFormat;

    @FXML private Label titleLabel;
    @FXML private Label durationLabel;
    @FXML private ComboBox<ComboBoxValueHolder<String>> formatsComboBox;
    @FXML private Button allFormatsButton;
    @FXML private Button audioButton;
    @FXML private CheckBox itemSelectedCheckBox;

    @FXML private ImageView thumbnailImageView;
    @FXML private StackPane thumbnailWrapperPane;

    public DownloadableItemComponentController(Stage stage, VideoInfo videoInfo) {
        this.stage = stage;
        this.videoInfo = videoInfo;
    }

    @FXML
    public void initialize() {
        initControlButtons();
        initLabels();
        initFormats();
        initThumbnail();
    }

    private void initControlButtons() {
        audioButton.setGraphic(new ImageView(Icon.AUDIOTRACK_SMALL.getImage()));
        audioButton.setTooltip(Tooltips.createNew("download.audio"));
        audioButton.setOnAction(e -> {
            AppUtils.resolveDownloadPath(stage).ifPresent(this::downloadAudio);
            e.consume();
        });

        allFormatsButton.setGraphic(new ImageView(Icon.FILTER_LIST_SMALL.getImage()));
        allFormatsButton.setTooltip(Tooltips.createNew("format.all"));
        allFormatsButton.setOnAction(e -> {
            new FormatsStage(videoInfo, customFormat, formatId -> {
                customFormat = formatId;
                ObservableList<ComboBoxValueHolder<String>> comboBoxItems = formatsComboBox.getItems();
                comboBoxItems.stream().filter(it -> it.getKey().equals(CUSTOM_FORMAT_LABEL)).findFirst().ifPresent(comboBoxItems::remove);
                ComboBoxValueHolder<String> valueHolder = new ComboBoxValueHolder<>(CUSTOM_FORMAT_LABEL, customFormat);
                comboBoxItems.add(valueHolder);
                formatsComboBox.getSelectionModel().select(valueHolder);
            }).modal(stage).show();
            e.consume();
        });

        formatsComboBox.prefHeightProperty().bind(allFormatsButton.heightProperty());
    }

    private void initLabels() {
        titleLabel.setText(videoInfo.getTitle());

        int durationSeconds = Objects.requireNonNullElse(videoInfo.getDuration(), 0);
        String formattedDuration = durationSeconds <= 0 ? StringUtils.EMPTY : DurationFormatUtils.formatDuration(durationSeconds * 1000L, "HH:mm:ss");
        durationLabel.setText(formattedDuration);
    }

    private void initFormats() {
        List<Integer> commonAvailableHeights = ListUtils.emptyIfNull(videoInfo.getFormats()).stream()
                .map(Format::getHeight)
                .filter(Objects::nonNull)
                .filter(it -> it > 0) // Should never happen, just a sanity check in case there's a bug in youtube-dl
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        ObservableList<ComboBoxValueHolder<String>> comboBoxItems = formatsComboBox.getItems();
        Integer autoSelectFormat = ctx.getConfigRegistry().get(AutoSelectFormatPref.class).getValue();
        ComboBoxValueHolder<String> selectedItem = null;
        for (Integer height : commonAvailableHeights) {
            ComboBoxValueHolder<String> item = new ComboBoxValueHolder<>(height + "p " + Resolution.getDescriptionByHeight(height), YouDlUtils.createFormatByHeight(height));
            comboBoxItems.add(item);

            if (selectedItem == null && height <= autoSelectFormat) {
                selectedItem = item;
            }
        }

        if (selectedItem == null) {
            formatsComboBox.getSelectionModel().selectFirst();
        } else {
            formatsComboBox.getSelectionModel().select(selectedItem);
        }

        adjustWidth();
    }

    /**
     * A small hack to make comboboxes the same width no matter when they get rendered
     */
    private void adjustWidth() {
        ComboBoxValueHolder<String> dummyItem = new ComboBoxValueHolder<>("99999p 8K Ultra HD", StringUtils.EMPTY);
        formatsComboBox.getItems().add(dummyItem);
        formatsComboBox.setOnShowing(e -> {
            ObservableList<ComboBoxValueHolder<String>> items = formatsComboBox.getItems();
            items.remove(dummyItem);
            formatsComboBox.setOnShowing(null);
        });
    }

    private void initThumbnail() {
        if (!ctx.getConfigRegistry().get(LoadThumbnailsPref.class).getValue()) {
            thumbnailWrapperPane.setVisible(false);
            thumbnailWrapperPane.setManaged(false);
            return;
        }

        Image image = new Image(AppUtils.normalizeThumbnailUrl(videoInfo));
        if (image.getException() == null) {
            thumbnailImageView.setCursor(Cursor.HAND);
            thumbnailImageView.setImage(image);
            thumbnailImageView.setOnMouseClicked(event -> {
                String thumbnailUrl = AppUtils.normalizeThumbnailUrlMaxRes(videoInfo);
                FileChooser fileChooser = new FileChooser();
                File recentDownloadPath = Path.of(ctx.getConfigRegistry().get(RecentDownloadPathPref.class).getValue()).toFile();
                if (recentDownloadPath.isDirectory()) {
                    fileChooser.setInitialDirectory(recentDownloadPath);
                }
                String extension = StringUtils.substringAfterLast(thumbnailUrl, ".");
                if (StringUtils.isNotBlank(extension)) {
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*." + extension, "*." + extension));
                }
                fileChooser.setInitialFileName("thumbnail_img");
                File downloadPath = fileChooser.showSaveDialog(stage);
                if (downloadPath != null) {
                    handleThumbnailImageViewMouseClick(event, URI.create(thumbnailUrl), downloadPath.toPath());
                }
            });
        }
    }

    private void handleThumbnailImageViewMouseClick(MouseEvent event, URI uri, Path downloadPath) {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofMinutes(1))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofFile(downloadPath))
                .exceptionally(throwable -> {
                    LOGGER.warn(throwable.getMessage(), throwable);
                    return null;
                });

        event.consume();
    }

    public void setSelectable(boolean selectable) {
        itemSelectedCheckBox.setVisible(selectable);
        itemSelectedCheckBox.setManaged(selectable);
    }

    public boolean isSelected() {
        return itemSelectedCheckBox.isSelected();
    }

    public void setSelected(boolean selected) {
        itemSelectedCheckBox.setSelected(selected);
    }

    public CheckBox getItemSelectedCheckBox() {
        return itemSelectedCheckBox;
    }

    public Downloadable getDownloadable() {
        BaseDownloadable downloadable = new BaseDownloadable();
        downloadable.setFormatId(formatsComboBox.getSelectionModel().getSelectedItem().getValue());
        downloadable.setTitle(titleLabel.getText());
        downloadable.setBaseUrl(videoInfo.getBaseUrl());
        return downloadable;
    }

    public void download(Path path) {
        Downloadable downloadable = getDownloadable();
        downloadable.setDownloadPath(path);
        historyManager.addToHistory(downloadable);
        queueManager.addItem(new QueueItem(downloadable));
    }

    public void downloadAudio(Path path) {
        ConfigRegistry configRegistry = ctx.getConfigRegistry();
        String format = configRegistry.get(AudioExtractionFormatPref.class).getValue();
        // Youtube-dl quality goes from 9 (worst) to 0 (best), thus needs adjusting to VDLs 0 (worst) - 9 (best)
        int quality = Math.abs(configRegistry.get(AudioExtractionQualityPref.class).getValue() - AudioFormat.BEST_QUALITY);
        Downloadable downloadable = getDownloadable();
        downloadable.setDownloadPath(path);
        downloadable.setFormatId("bestaudio"); // No need to download video if user only wants to extract audio
        downloadable.setPostprocessingSteps(List.of(ExtractAudioPostprocessing.newInstance(format, quality)));
        historyManager.addToHistory(downloadable);
        queueManager.addItem(new QueueItem(downloadable));
    }
}
