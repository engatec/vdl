package com.github.engatec.vdl;

import com.github.engatec.vdl.controller.MainController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.general.LanguageConfigItem;
import com.github.engatec.vdl.ui.Dialogs;
import javafx.application.Application;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        UiManager.loadStage(UiComponent.MAIN, stage, param -> new MainController(stage));
        stage.setTitle("VDL - Video downloader");
        stage.setOnCloseRequest(this::handleCloseRequest);
        stage.show();
    }

    @Override
    public void stop() {
        ApplicationContext.INSTANCE.shutdownExecutors();
    }

    /**
     * Confirmation on app close if queue items being downloaded
     */
    private void handleCloseRequest(WindowEvent e) {
        if (QueueManager.INSTANCE.hasItemsInProgress()) {
            ButtonBar.ButtonData result = Dialogs.warningWithYesNoButtons("stage.main.dialog.close.queuehasitemsinprogress")
                    .map(ButtonType::getButtonData)
                    .orElse(ButtonBar.ButtonData.YES);
            if (result == ButtonBar.ButtonData.NO) {
                e.consume();
            }
        }
    }

    public static void main(String[] args) {
        Janitor.cleanUp();
        setLanguage();
        QueueManager.INSTANCE.restoreQueue();
        launch(args);
        QueueManager.INSTANCE.persistQueue();
    }

    private static void setLanguage() {
        Language language = Language.getByLocaleLanguage(ConfigManager.INSTANCE.getValue(new LanguageConfigItem()));
        ApplicationContext.INSTANCE.setLanguage(language);
    }
}
