package com.github.engatec.vdl;

import java.util.List;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.core.YoutubeDlManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistryImpl;
import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YoutubeDlStartupUpdatesCheckPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YtdlpStartupUpdatesCheckPref;
import com.github.engatec.vdl.ui.stage.MainStage;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        loadFonts();

        ApplicationContext.INSTANCE.setConfigRegistry(new ConfigRegistryImpl());
        setLanguage();
        ApplicationContext.INSTANCE.setManagers(List.of(
                new DbManager("jdbc:sqlite:" + ApplicationContext.DB_PATH),
                new QueueManager(),
                HistoryManager.INSTANCE,
                SubscriptionsManager.INSTANCE
        ));

        new MainStage(stage).show();

        checkUpdates(stage);
        SubscriptionsManager.INSTANCE.updateAllSubscriptions();
    }

    @Override
    public void stop() {
        HistoryManager.INSTANCE.stripHistory();
        AppExecutors.shutdownExecutors();
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
        ApplicationContext ctx = ApplicationContext.INSTANCE;
        Language language = Language.getByLocaleCode(ctx.getConfigRegistry().get(LanguagePref.class).getValue());
        ctx.setLanguage(language);
    }

    private void checkUpdates(Stage stage) {
        Boolean needCheckYoutubeDlUpdate = ApplicationContext.INSTANCE.getConfigRegistry().get(YoutubeDlStartupUpdatesCheckPref.class).getValue();
        if (needCheckYoutubeDlUpdate) {
            YoutubeDlManager.INSTANCE.checkLatestYoutubeDlVersion(stage);
        }

        Boolean needCheckYtdlpUpdate = ApplicationContext.INSTANCE.getConfigRegistry().get(YtdlpStartupUpdatesCheckPref.class).getValue();
        if (needCheckYtdlpUpdate) {
            YoutubeDlManager.INSTANCE.checkLatestYtdlpVersion(stage);
        }
    }
}
