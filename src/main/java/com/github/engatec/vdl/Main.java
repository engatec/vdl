package com.github.engatec.vdl;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.general.LanguageConfigItem;
import com.github.engatec.vdl.stage.MainStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new MainStage(stage).show();
    }

    @Override
    public void stop() {
        ApplicationContext.INSTANCE.shutdownExecutors();
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
