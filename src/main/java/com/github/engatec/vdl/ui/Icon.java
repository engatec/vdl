package com.github.engatec.vdl.ui;

import java.util.Objects;

import javafx.scene.image.Image;

public enum Icon {

    AUDIOTRACK_SMALL("/assets/icons/audiotrack_18dp.png"),
    SUBTITLES_SMALL("/assets/icons/subtitles_18dp.png"),
    COLLAPSE_MEDIUM("/assets/icons/collapse_24dp.png"),
    DELETE_SMALL("/assets/icons/delete_18dp.png"),
    DOWNLOAD_SMALL("/assets/icons/download_icon_18dp.png"),
    EXPAND_MEDIUM("/assets/icons/expand_24dp.png"),
    FILTER_LIST_SMALL("/assets/icons/filter_list_18dp.png"),
    FOLDER_SMALL("/assets/icons/folder_18dp.png"),
    HELP_MEDIUM("/assets/icons/help_24dp.png"),
    HISTORY_SMALL("/assets/icons/history_icon_18dp.png"),
    REFRESH_SMALL("/assets/icons/refresh_18dp.png"),
    SEARCH_SMALL("/assets/icons/search_icon_18dp.png"),
    SETTINGS_MEDIUM("/assets/icons/settings_24dp.png"),
    SUBSCRIPTIONS_SMALL("/assets/icons/subscriptions_18dp.png");

    private final Image image;

    Icon(String path) {
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public Image getImage() {
        return image;
    }
}
