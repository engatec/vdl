package com.github.engatec.vdl;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YoutubeDlStartupUpdatesCheckPref;
import com.github.engatec.vdl.ui.stage.MainStage;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        loadFonts();
        setLanguage();
        QueueManager.INSTANCE.restoreQueue();
        new MainStage(stage).show();

        Boolean needCheckYoutubeDlUpdate = ConfigRegistry.get(YoutubeDlStartupUpdatesCheckPref.class).getValue();
        if (needCheckYoutubeDlUpdate) {
            YoutubeDlManager.INSTANCE.checkLatestYoutubeDlVersion(stage);
        }
    }

    @Override
    public void stop() {
        AppExecutors.shutdownExecutors();
        QueueManager.INSTANCE.persistQueue();
        HistoryManager.INSTANCE.persistHistory();
    }

    public static void main(String[] args) {
        Janitor.cleanUp();
        launch(args);
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Roboto-Regular.ttf"), 0);
        Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Roboto-Bold.ttf"), 0);
    }

    private void setLanguage() {
        Language language = Language.getByLocaleCode(ConfigRegistry.get(LanguagePref.class).getValue());
        ApplicationContext.INSTANCE.setLanguage(language);
    }
}
