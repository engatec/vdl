package com.github.engatec.vdl.controller;

import java.util.concurrent.CompletableFuture;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.util.AppUtils;
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
    @FXML private Label youtubeDlVersionLabel;
    @FXML private ProgressIndicator youtubeDlVersionProgress;
    @FXML private Button youtubeDlUpdateBtn;
    @FXML private Label ytdlpVersionLabel;
    @FXML private ProgressIndicator ytdlpVersionProgress;
    @FXML private Button ytdlpUpdateBtn;
    @FXML private Button closeBtn;

    private AboutController() {
    }

    public AboutController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        stage.setTitle(ctx.getLocalizedString("stage.about.title"));
        vdlVersionLabel.setText(String.format(ctx.getLocalizedString("stage.about.label.version.vdl"), getVdlVersion()));
        setYoutubeDlVersionLabel();
        youtubeDlUpdateBtn.setOnAction(this::handleYoutubeDlUpdateButtonClick);
        setYtdlpVersionLabel();
        ytdlpUpdateBtn.setOnAction(this::handleYtdlpUpdateButtonClick);
        closeBtn.setOnAction(this::handleCloseButtonClick);
    }

    private String getVdlVersion() {
        return StringUtils.defaultIfBlank(getClass().getPackage().getImplementationVersion(), UNKNOWN_VERSION);
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

    private void handleYoutubeDlUpdateButtonClick(ActionEvent event) {
        AppUtils.updateYoutubeDl(stage, () -> {
            String v = StringUtils.defaultIfBlank(YoutubeDlManager.INSTANCE.getCurrentVersion(Engine.YOUTUBE_DL), UNKNOWN_VERSION);
            String label = ctx.getLocalizedString("stage.about.label.version.youtubedl") + " " + v;
            youtubeDlVersionLabel.setText(label);
        });
        event.consume();
    }

    private void handleYtdlpUpdateButtonClick(ActionEvent event) {
        AppUtils.updateYtdlp(stage, () -> {
            String v = StringUtils.defaultIfBlank(YoutubeDlManager.INSTANCE.getCurrentVersion(Engine.YT_DLP), UNKNOWN_VERSION);
            String label = ctx.getLocalizedString("stage.about.label.version.ytdlp") + " " + v;
            ytdlpVersionLabel.setText(label);
        });
        event.consume();
    }

    private void handleCloseButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
