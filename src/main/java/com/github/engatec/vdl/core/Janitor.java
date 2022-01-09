package com.github.engatec.vdl.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import com.github.engatec.vdl.Main;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Janitor {

    private static final Logger LOGGER = LogManager.getLogger(Janitor.class);

    public static void cleanUp() {
        try {
            fixPortability();
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }

    private static void fixPortability() throws Exception {
        if (!Boolean.parseBoolean(System.getProperty("app.portable"))) {
            return;
        }

        fixPreferences();
        fixAppData();
    }

    private static void fixPreferences() throws BackingStoreException {
        if (!Preferences.userRoot().nodeExists("/com/github/engatec/vdl")) {
            return;
        }

        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        for (String key : preferences.keys()) {
            ConfigManager.INSTANCE.setValue(key, preferences.get(key, null));
        }
        preferences.parent().removeNode();
        preferences.flush();
    }

    private static void fixAppData() throws IOException {
        Path appDataDir = SystemUtils.getUserHome().toPath().resolve(".vdl");
        if (Files.notExists(appDataDir)) {
            return;
        }

        Path appBinariesDir = Path.of(StringUtils.defaultString(System.getProperty("app.dir"), StringUtils.EMPTY));
        for (String it : List.of("queue.vdl", "history.vdl", "subscriptions.vdl")) {
            try {
                Files.copy(appDataDir.resolve(it), appBinariesDir.resolve(it));
            } catch (Exception ignored) {
                // Ignore exception and go to the next file
            }
        }

        FileUtils.forceDelete(appDataDir.toFile());
    }
}
