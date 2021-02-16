package com.github.engatec.vdl;

import com.github.engatec.vdl.controller.MainController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.general.LanguageConfigItem;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        UiManager.loadStage(UiComponent.MAIN, stage, param -> new MainController(stage));
        stage.setTitle("VDL - Video downloader");
        stage.show();
    }

    @Override
    public void stop() {
        ApplicationContext.INSTANCE.cleanUp();
    }

    public static void main(String[] args) {
        Janitor.cleanUpPropetiesFile();
        setLanguage();
        launch(args);
    }

    private static void setLanguage() {
        Language language = Language.getByLocaleLanguage(ConfigManager.INSTANCE.getValue(new LanguageConfigItem()));
        ApplicationContext.INSTANCE.setLanguage(language);
    }
}
