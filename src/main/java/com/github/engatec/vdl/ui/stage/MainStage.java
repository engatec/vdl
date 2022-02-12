package com.github.engatec.vdl.ui.stage;

import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.preference.ConfigRegistry;
import com.github.engatec.vdl.preference.property.ui.MainWindowHeightConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowPosXConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowPosYConfigProperty;
import com.github.engatec.vdl.preference.property.ui.MainWindowWidthConfigProperty;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.controller.stage.MainController;
import javafx.beans.property.DoubleProperty;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import static com.github.engatec.vdl.model.DownloadStatus.IN_PROGRESS;

public class MainStage extends AppStage {

    private static final int STAGE_MIN_WIDTH = 500;
    private static final int STAGE_MIN_HEIGHT = 300;

    private final QueueManager queueManager = ctx.getManager(QueueManager.class);

    public MainStage(Stage stage) {
        super(stage);
        init();
    }

    @Override
    protected void init() {
        super.init();
        stage.setTitle("VDL - Video downloader");

        ConfigRegistry configRegistry = ctx.getConfigRegistry();

        stage.setMinWidth(STAGE_MIN_WIDTH);
        DoubleProperty widthProperty = configRegistry.get(MainWindowWidthConfigProperty.class).getProperty();
        stage.setWidth(widthProperty.getValue());
        widthProperty.bind(stage.widthProperty());

        stage.setMinHeight(STAGE_MIN_HEIGHT);
        DoubleProperty heightProperty = configRegistry.get(MainWindowHeightConfigProperty.class).getProperty();
        stage.setHeight(heightProperty.getValue());
        heightProperty.bind(stage.heightProperty());

        DoubleProperty xProperty = configRegistry.get(MainWindowPosXConfigProperty.class).getProperty();
        if (xProperty.getValue() >= 0) {
            stage.setX(xProperty.getValue());
        }
        xProperty.bind(stage.xProperty());

        DoubleProperty yProperty = configRegistry.get(MainWindowPosYConfigProperty.class).getProperty();
        if (yProperty.getValue() >= 0) {
            stage.setY(yProperty.getValue());
        }
        yProperty.bind(stage.yProperty());

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
        if (queueManager.hasItem(it -> it.getStatus() == IN_PROGRESS)) {
            Dialogs.warningWithYesNoButtons("stage.main.dialog.close.queuehasitemsinprogress", null, e::consume);
        }
    }
}
