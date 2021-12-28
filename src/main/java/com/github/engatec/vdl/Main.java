package com.github.engatec.vdl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.engatec.vdl.core.AppExecutors;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.HistoryManager;
import com.github.engatec.vdl.core.Janitor;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.preferences.ConfigRegistryImpl;
import com.github.engatec.vdl.db.DbManager;
import com.github.engatec.vdl.model.preferences.wrapper.general.VdlStartupUpdatesCheckPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YoutubeDlStartupUpdatesCheckPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.YtdlpStartupUpdatesCheckPref;
import com.github.engatec.vdl.service.newversion.Updater;
import com.github.engatec.vdl.service.newversion.VdlUpdater;
import com.github.engatec.vdl.service.newversion.YoutubeDlUpdater;
import com.github.engatec.vdl.service.newversion.YtDlpUpdater;
import com.github.engatec.vdl.ui.stage.MainStage;
import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        loadFonts();

        ApplicationContext.init(
                Path.of(StringUtils.defaultString(System.getProperty("app.dir"), StringUtils.EMPTY)),
                SystemUtils.getUserHome().toPath().resolve(".vdl"),
                "data.db",
                new ConfigRegistryImpl(),
                List.of(
                        new DbManager(),
                        new QueueManager(),
                        new HistoryManager(),
                        new SubscriptionsManager()
                )
        );

        new MainStage(stage).show();

        checkUpdates(stage);
        ApplicationContext.getInstance().getManager(SubscriptionsManager.class).updateAllSubscriptions();
    }

    @Override
    public void stop() {
        ApplicationContext.getInstance().getManager(HistoryManager.class).stripHistory();
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

    private void checkUpdates(Stage stage) {
        ConfigRegistry configRegistry = ApplicationContext.getInstance().getConfigRegistry();
        List<Updater> updaters = new ArrayList<>();

        Boolean needCheckVdlUpdate = configRegistry.get(VdlStartupUpdatesCheckPref.class).getValue();
        if (needCheckVdlUpdate) {
            updaters.add(new VdlUpdater(stage));
        }

        Boolean needCheckYoutubeDlUpdate = configRegistry.get(YoutubeDlStartupUpdatesCheckPref.class).getValue();
        if (needCheckYoutubeDlUpdate) {
            updaters.add(new YoutubeDlUpdater(stage));
        }

        Boolean needCheckYtdlpUpdate = configRegistry.get(YtdlpStartupUpdatesCheckPref.class).getValue();
        if (needCheckYtdlpUpdate) {
            updaters.add(new YtDlpUpdater(stage));
        }

        updaters.forEach(Updater::update);
    }
}
