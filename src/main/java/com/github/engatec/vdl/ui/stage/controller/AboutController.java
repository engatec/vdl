package com.github.engatec.vdl.ui.stage.controller;

import java.util.concurrent.CompletableFuture;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.service.newversion.VdlUpdater;
import com.github.engatec.vdl.service.newversion.YoutubeDlUpdater;
import com.github.engatec.vdl.service.newversion.YtDlpUpdater;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class AboutController extends StageAwareController {

    private static final String UNKNOWN_VERSION = "unknown";

    private final ApplicationContext ctx = ApplicationContext.getInstance();

    @FXML private Label vdlVersionLabel;
    @FXML private Button vdlUpdateBtn;
    @FXML private ProgressIndicator vdlUpdateProgress;

    @FXML private Label youtubeDlVersionLabel;
    @FXML private ProgressIndicator youtubeDlVersionProgress;
    @FXML private Button youtubeDlUpdateBtn;
    @FXML private ProgressIndicator youtubeDlUpdateProgress;

    @FXML private Label ytdlpVersionLabel;
    @FXML private ProgressIndicator ytdlpVersionProgress;
    @FXML private Button ytdlpUpdateBtn;
    @FXML private ProgressIndicator ytDlpUpdateProgress;

    @FXML private Button closeBtn;

    private AboutController() {
    }

    public AboutController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        vdlVersionLabel.setText(String.format(ctx.getLocalizedString("stage.about.label.version.vdl"), ctx.getAppVersion()));
        vdlUpdateProgress.managedProperty().bind(vdlUpdateBtn.visibleProperty().not());
        vdlUpdateProgress.visibleProperty().bind(vdlUpdateBtn.visibleProperty().not());
        vdlUpdateBtn.setOnAction(this::handleVdlUpdateButtonClick);

        setYoutubeDlVersionLabel();
        youtubeDlUpdateProgress.managedProperty().bind(youtubeDlUpdateBtn.visibleProperty().not());
        youtubeDlUpdateProgress.visibleProperty().bind(youtubeDlUpdateBtn.visibleProperty().not());
        youtubeDlUpdateBtn.setOnAction(this::handleYoutubeDlUpdateButtonClick);

        setYtdlpVersionLabel();
        ytDlpUpdateProgress.managedProperty().bind(ytdlpUpdateBtn.visibleProperty().not());
        ytDlpUpdateProgress.visibleProperty().bind(ytdlpUpdateBtn.visibleProperty().not());
        ytdlpUpdateBtn.setOnAction(this::handleYtdlpUpdateButtonClick);

        closeBtn.setOnAction(this::handleCloseButtonClick);
    }

    private void setYoutubeDlVersionLabel() {
        CompletableFuture.runAsync(() -> {
            String v = StringUtils.defaultIfBlank(YoutubeDlManager.INSTANCE.getCurrentVersion(Engine.YOUTUBE_DL), UNKNOWN_VERSION);
            String label = ctx.getLocalizedString("stage.about.label.version.youtubedl") + " " + v;
            Platform.runLater(() -> {
                youtubeDlVersionProgress.setVisible(false);
                youtubeDlVersionLabel.setText(label);
            });
        });
    }

    private void setYtdlpVersionLabel() {
        CompletableFuture.runAsync(() -> {
            String v = StringUtils.defaultIfBlank(YoutubeDlManager.INSTANCE.getCurrentVersion(Engine.YT_DLP), UNKNOWN_VERSION);
            String label = ctx.getLocalizedString("stage.about.label.version.ytdlp") + " " + v;
            Platform.runLater(() -> {
                ytdlpVersionProgress.setVisible(false);
                ytdlpVersionLabel.setText(label);
            });
        });
    }

    private void handleVdlUpdateButtonClick(ActionEvent event) {
        vdlUpdateBtn.setVisible(false);
        VdlUpdater vdlUpdater = new VdlUpdater(stage);
        vdlUpdater.setOnComplete(() -> vdlUpdateBtn.setVisible(true));
        vdlUpdater.update();
        event.consume();
    }

    private void handleYoutubeDlUpdateButtonClick(ActionEvent event) {
        youtubeDlUpdateBtn.setVisible(false);
        YoutubeDlUpdater youtubeDlUpdater = new YoutubeDlUpdater(stage);
        youtubeDlUpdater.setOnSucceeded(v -> {
            String label = ctx.getLocalizedString("stage.about.label.version.youtubedl") + " " + v;
            youtubeDlVersionLabel.setText(label);
        });
        youtubeDlUpdater.setOnComplete(() -> youtubeDlUpdateBtn.setVisible(true));
        youtubeDlUpdater.update();
        event.consume();
    }

    private void handleYtdlpUpdateButtonClick(ActionEvent event) {
        ytdlpUpdateBtn.setVisible(false);
        YtDlpUpdater ytDlpUpdater = new YtDlpUpdater(stage);
        ytDlpUpdater.setOnSucceeded(v -> {
            String label = ctx.getLocalizedString("stage.about.label.version.ytdlp") + " " + v;
            ytdlpVersionLabel.setText(label);
        });
        ytDlpUpdater.setOnComplete(() -> ytdlpUpdateBtn.setVisible(true));
        ytDlpUpdater.update();
        event.consume();
    }

    private void handleCloseButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
