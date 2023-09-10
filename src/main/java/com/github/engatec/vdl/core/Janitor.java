package com.github.engatec.vdl.core;

import com.github.engatec.vdl.preference.ConfigManager;

public class Janitor {

    public static void cleanUp() {
        removeObsoleteProperties();
    }

    private static void removeObsoleteProperties() {
        try {
            ConfigManager cm = ConfigManager.INSTANCE;
            cm.remove("general.youtube_dl_startup_updates_check");
            cm.remove("misc.downloader");
            cm.flush();
        } catch (Throwable t) {
            // Catch and do nothing. Janitor must not cause app failure under any circumstances
        }
    }
}
