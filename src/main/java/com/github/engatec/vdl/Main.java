package com.github.engatec.vdl;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.ui.stage.MainStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new MainStage(stage).show();
    }

    @Override
    public void stop() {
        AppExecutors.shutdownExecutors();
    }

    public static void main(String[] args) {
        Janitor.cleanUp();
        setLanguage();
        QueueManager.INSTANCE.restoreQueue();
        launch(args);
        QueueManager.INSTANCE.persistQueue();
    }

    private static void setLanguage() {
        Language language = Language.getByLocaleLanguage(ConfigRegistry.get(LanguagePref.class).getValue());
        ApplicationContext.INSTANCE.setLanguage(language);
    }
}
