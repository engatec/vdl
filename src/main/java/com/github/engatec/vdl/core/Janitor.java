package com.github.engatec.vdl.core;

import com.github.engatec.vdl.core.preferences.ConfigManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Janitor {

    private static final Logger LOGGER = LogManager.getLogger(Janitor.class);

    public static void cleanUp() {
        try {
            cleanUpOldConfig();
        } catch (Exception e) {
            LOGGER.warn(e);
        }
    }

    private static void cleanUpOldConfig() {
        ConfigManager cfg = ConfigManager.INSTANCE;
        cfg.remove("general.autodownload");
        cfg.remove("general.autodownloadFormat");
        cfg.remove("general.skipDownloadableDetailsSearch");
        cfg.remove("misc.queueAutostartDownload");
    }
}
