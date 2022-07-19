package com.github.engatec.vdl.ui.stage.controller;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.Subtitle;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.preference.property.misc.RecentDownloadPathConfigProperty;
import com.github.engatec.vdl.service.SubtitlesDownloadService;
import com.github.engatec.vdl.ui.data.CheckBoxGroup;
import com.github.engatec.vdl.ui.helper.Dialogs;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Labeled;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubtitlesController {

    private static final Logger LOGGER = LogManager.getLogger(SubtitlesController.class);

    private Stage stage;
    private VideoInfo videoInfo;

    private CheckBoxGroup checkBoxGroup;
    @FXML private CheckBox selectAllCheckBox;
    @FXML private VBox subtitlesVBox;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    public SubtitlesController() {
    }

    public SubtitlesController(Stage stage, VideoInfo videoInfo) {
        this.stage = stage;
        this.videoInfo = videoInfo;
    }

    @FXML
    public void initialize() {
        selectAllCheckBox.setManaged(videoInfo.subtitles().size() > 1);
        selectAllCheckBox.visibleProperty().bind(selectAllCheckBox.managedProperty());

        checkBoxGroup = new CheckBoxGroup(selectAllCheckBox);

        for (Subtitle s : videoInfo.subtitles()) {
            CheckBox cb = new CheckBox(s.language() + " - " + s.isoCode());
            subtitlesVBox.getChildren().add(cb);
            checkBoxGroup.add(cb);
        }

        saveButton.setOnMouseClicked(this::handleSaveButtonClick);
        cancelButton.setOnMouseClicked(this::handleCancelButtonClick);
    }

    private void handleSaveButtonClick(MouseEvent e) {
        e.consume();

        Set<String> selectedSubtitles = checkBoxGroup.getSelected().stream()
                .map(Labeled::getText)
                .map(it -> StringUtils.substringAfterLast(it, " - "))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(selectedSubtitles)) {
            Dialogs.error("subtitles.notselected");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        File recentDownloadPath = Path.of(ApplicationContext.getInstance().getConfigRegistry().get(RecentDownloadPathConfigProperty.class).getValue()).toFile();
        if (recentDownloadPath.isDirectory()) {
            fileChooser.setInitialDirectory(recentDownloadPath);
        }
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.srt", "*.srt"));
        fileChooser.setInitialFileName("subtitles");
        File downloadPath = fileChooser.showSaveDialog(stage);
        if (downloadPath != null) {
            SubtitlesDownloadService subtitlesDownloadService = new SubtitlesDownloadService(videoInfo.baseUrl(), selectedSubtitles, downloadPath.toPath());
            subtitlesDownloadService.setOnFailed(event -> {
                Throwable exception = event.getSource().getException();
                if (exception != null) {
                    LOGGER.warn(exception.getMessage(), exception);
                }
                Dialogs.error("subtitles.downloading.error");
            });
            Dialogs.progress("subtitles.downloading", stage, subtitlesDownloadService);
        }
        stage.close();
    }

    private void handleCancelButtonClick(MouseEvent e) {
        stage.close();
        e.consume();
    }
}
