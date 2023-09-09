package com.github.engatec.vdl.ui.stage.controller;

import java.util.concurrent.CompletableFuture;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Engine;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.service.newversion.VdlUpdater;
import com.github.engatec.vdl.service.newversion.YtDlpUpdater;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class AboutController extends StageAwareController {

    private static final String UNKNOWN_VERSION = "unknown";

    private final ApplicationContext ctx = ApplicationContext.getInstance();

    @FXML private Label vdlVersionLabel;
    @FXML private Button vdlUpdateBtn;
    @FXML private ProgressIndicator vdlUpdateProgress;

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
        initializeButtonProgressProperty(vdlUpdateProgress, vdlUpdateBtn);
        vdlUpdateBtn.setOnAction(this::handleVdlUpdateButtonClick);

        setYtdlpVersionLabel();
        initializeButtonProgressProperty(ytDlpUpdateProgress, ytdlpUpdateBtn);
        initializeVersionProgressProperty(ytdlpVersionProgress, ytdlpUpdateBtn);
        ytdlpUpdateBtn.setOnAction(this::handleYtdlpUpdateButtonClick);

        closeBtn.setOnAction(this::handleCloseButtonClick);
    }

    private void initializeButtonProgressProperty(ProgressIndicator progress, Button button) {
        BooleanBinding buttonInvisibleProperty = button.visibleProperty().not();
        progress.managedProperty().bind(buttonInvisibleProperty);
        progress.visibleProperty().bind(buttonInvisibleProperty);

        ReadOnlyDoubleProperty buttonHeightProperty = button.heightProperty();
        progress.minHeightProperty().bind(buttonHeightProperty);
        progress.prefHeightProperty().bind(buttonHeightProperty);
        progress.maxHeightProperty().bind(buttonHeightProperty);
    }

    private void initializeVersionProgressProperty(ProgressIndicator progress, Region node) {
        ReadOnlyDoubleProperty nodeHeightProperty = node.heightProperty();
        progress.minHeightProperty().bind(nodeHeightProperty);
        progress.prefHeightProperty().bind(nodeHeightProperty);
        progress.maxHeightProperty().bind(nodeHeightProperty);
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
