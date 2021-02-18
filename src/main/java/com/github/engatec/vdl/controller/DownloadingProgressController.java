package com.github.engatec.vdl.controller;


import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.worker.DownloadTask;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadingProgressController extends StageAwareController {

    private static final Logger LOGGER = LogManager.getLogger(DownloadingProgressController.class);

    private Downloadable downloadable;

    @FXML private Label downloadingProgressLabel;
    @FXML private TextArea downloadTextArea;
    @FXML private Button progressCancelBtn;
    @FXML private Button progressDoneBtn;

    private DownloadingProgressController() {
    }

    public DownloadingProgressController(Stage stage, Downloadable downloadable) {
        super(stage);
        this.downloadable = downloadable;
    }

    @FXML
    public void initialize() {
        downloadingProgressLabel.setText(ApplicationContext.INSTANCE.getResourceBundle().getString("download.progress.label.inprogress"));
        startDownloading();
    }

    private void startDownloading() {
        DownloadTask task = new DownloadTask(downloadable, downloadTextArea);

        task.setOnSucceeded(event -> {
            setDownloadingResult("download.progress.label.finished");
            event.consume();
        });

        task.setOnFailed(event -> {
            Throwable ex = event.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
            setDownloadingResult("download.progress.label.error");
            event.consume();
        });

        task.setOnCancelled(event -> {
            setDownloadingResult("download.progress.label.cancelled");
            event.consume();
        });

        ReadOnlyBooleanProperty taskRunningProperty = task.runningProperty();
        progressDoneBtn.prefWidthProperty().bind(progressCancelBtn.widthProperty());
        progressCancelBtn.visibleProperty().bind(taskRunningProperty);
        progressCancelBtn.managedProperty().bind(taskRunningProperty);
        progressDoneBtn.visibleProperty().bind(taskRunningProperty.not());
        progressDoneBtn.managedProperty().bind(taskRunningProperty.not());

        progressCancelBtn.setOnAction(event -> {
            task.cancel();
            event.consume();
        });

        progressDoneBtn.setOnAction(event -> {
            stage.close();
            event.consume();
        });

        ApplicationContext.INSTANCE.runTaskAsync(task);
    }

    private void setDownloadingResult(String mesageKey) {
        String downloadingResult = ApplicationContext.INSTANCE.getResourceBundle().getString(mesageKey);
        downloadingProgressLabel.setText(downloadingResult);
        downloadTextArea.appendText(StringUtils.LF);
        downloadTextArea.appendText(downloadingResult);
    }
}
