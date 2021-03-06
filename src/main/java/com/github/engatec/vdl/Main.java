package com.github.engatec.vdl;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistryImpl;
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
        initConfig();
        loadFonts();
        setLanguage();

        QueueManager.INSTANCE.restore();
        HistoryManager.INSTANCE.restore();
        SubscriptionsManager.INSTANCE.restore();

        new MainStage(stage).show();

        Boolean needCheckYoutubeDlUpdate = ApplicationContext.INSTANCE.getConfigRegistry().get(YoutubeDlStartupUpdatesCheckPref.class).getValue();
        if (needCheckYoutubeDlUpdate) {
            YoutubeDlManager.INSTANCE.checkLatestYoutubeDlVersion(stage);
        }
        SubscriptionsManager.INSTANCE.updateAllSubscriptions();
    }

    @Override
    public void stop() {
        AppExecutors.shutdownExecutors();
        QueueManager.INSTANCE.persist();
        HistoryManager.INSTANCE.persist();
        SubscriptionsManager.INSTANCE.persist();
    }

    public static void main(String[] args) {
        Janitor.cleanUp();
        launch(args);
    }

    private void initConfig() {
        ApplicationContext.INSTANCE.setConfigRegistry(new ConfigRegistryImpl());
    }

    private void loadFonts() {
        Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Roboto-Regular.ttf"), 0);
        Font.loadFont(getClass().getResourceAsStream("/assets/fonts/Roboto-Bold.ttf"), 0);
    }

    private void setLanguage() {
        ApplicationContext ctx = ApplicationContext.INSTANCE;
        Language language = Language.getByLocaleCode(ctx.getConfigRegistry().get(LanguagePref.class).getValue());
        ctx.setLanguage(language);
    }
}
