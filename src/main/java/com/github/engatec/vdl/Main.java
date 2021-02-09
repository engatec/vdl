package com.github.engatec.vdl;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.ConfigManager;
import com.github.engatec.vdl.core.ConfigProperty;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.model.Language;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        UiManager.loadStage(UiComponent.MAIN, stage);
        stage.setTitle("VDL - Video downloader");
        stage.show();
    }

    @Override
    public void stop() {
        ApplicationContext.INSTANCE.cleanUp();
    }

    public static void main(String[] args) {
        loadConfig();
        setLanguage();
        launch(args);
    }

    private static void loadConfig() {
        ConfigManager.INSTANCE.loadConfig();
    }

    private static void setLanguage() {
        Language language = Language.getByLocaleLanguage(ConfigManager.INSTANCE.getValue(ConfigProperty.LANGUAGE));
        ApplicationContext.INSTANCE.setLanguage(language);
    }
}
