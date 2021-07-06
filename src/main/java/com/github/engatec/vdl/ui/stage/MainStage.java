package com.github.engatec.vdl.ui.stage;

import com.github.engatec.vdl.controller.stage.MainController;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import static com.github.engatec.vdl.model.DownloadStatus.IN_PROGRESS;

public class MainStage extends AppStage {

    private static final int STAGE_MIN_WIDTH = 500;
    private static final int STAGE_MIN_HEIGHT = 300;
    private static final int STAGE_PREF_WIDTH = 800;
    private static final int STAGE_PREF_HEIGHT = 600;

    public MainStage(Stage stage) {
        super(stage);
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setTitle("VDL - Video downloader");
        stage.setWidth(STAGE_PREF_WIDTH);
        stage.setHeight(STAGE_PREF_HEIGHT);
        stage.setMinWidth(STAGE_MIN_WIDTH);
        stage.setMinHeight(STAGE_MIN_HEIGHT);
        stage.setOnCloseRequest(this::handleCloseRequest);
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/main.fxml";
    }

    @Override
    protected Callback<Class<?>, Object> getControllerFactory(Stage stage) {
        return param -> new MainController(stage);
    }

    /**
     * Confirmation on app close if queue items being downloaded
     */
    private void handleCloseRequest(WindowEvent e) {
        if (QueueManager.INSTANCE.hasItem(it -> it.getStatus() == IN_PROGRESS)) {
            Dialogs.warningWithYesNoButtons("stage.main.dialog.close.queuehasitemsinprogress", null, e::consume);
        }
    }
}
