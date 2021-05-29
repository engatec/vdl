package com.github.engatec.vdl.controller.component.search;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
import com.github.engatec.vdl.ui.Icon;
import com.github.engatec.vdl.ui.Tooltips;
import com.github.engatec.vdl.ui.data.ComboBoxValueHolder;
import com.github.engatec.vdl.ui.stage.FormatsStage;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.util.YouDlUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

public class DownloadableItemComponentController extends HBox {

    private static final String CUSTOM_FORMAT_LABEL = "Custom format";

    private final Stage stage;
    private final VideoInfo videoInfo;
    private String customFormat;

    @FXML private Label titleLabel;
    @FXML private Label durationLabel;
    @FXML private ComboBox<ComboBoxValueHolder<String>> formatsComboBox;
    @FXML private Button allFormatsButton;
    @FXML private Button audioButton;
    @FXML private CheckBox itemSelectedCheckBox;

    public DownloadableItemComponentController(Stage stage, VideoInfo videoInfo) {
        this.stage = stage;
        this.videoInfo = videoInfo;
    }

    @FXML
    public void initialize() {
        initControlButtons();
        initLabels();
        initFormats();
    }

    private void initControlButtons() {
        audioButton.setGraphic(new ImageView(Icon.AUDIOTRACK_SMALL.getImage()));
        audioButton.setTooltip(Tooltips.createNew("download.audio"));
        audioButton.setOnAction(e -> {
            AppUtils.resolveDownloadPath(stage).ifPresent(path -> {
                String format = ConfigRegistry.get(AudioExtractionFormatPref.class).getValue();
                // Youtube-dl quality goes from 9 (worst) to 0 (best), thus needs adjusting to VDLs 0 (worst) - 9 (best)
                int quality = Math.abs(ConfigRegistry.get(AudioExtractionQualityPref.class).getValue() - AudioFormat.BEST_QUALITY);
                Downloadable audioDownloadable = getDownloadable();
                audioDownloadable.setDownloadPath(path);
                audioDownloadable.setFormatId("bestaudio"); // No need to download video if user only wants to extract audio
                audioDownloadable.setPostprocessingSteps(List.of(ExtractAudioPostprocessing.newInstance(format, quality)));
                QueueManager.INSTANCE.addItem(new QueueItem(audioDownloadable));
            });
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
                .collect(Collectors.toList());

        ObservableList<ComboBoxValueHolder<String>> comboBoxItems = formatsComboBox.getItems();
        Integer autoSelectFormat = ConfigRegistry.get(AutoSelectFormatPref.class).getValue();
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

    private ContextMenu createContextMenu() {
        /*ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        ContextMenu ctxMenu = new ContextMenu();

        MenuItem postprocessingMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.contextmenu.postprocessing"));
        postprocessingMenuItem.setOnAction(e -> {
            new PostprocessingStage(downloadable).modal(stage).showAndWait();
            e.consume();
        });
        ctxMenu.getItems().add(postprocessingMenuItem);

        return ctxMenu;*/
        return null;
    }
}
