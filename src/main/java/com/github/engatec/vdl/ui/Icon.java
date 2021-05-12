package com.github.engatec.vdl.ui;

import java.util.Objects;

import javafx.scene.image.Image;

public enum Icon {

    AUDIOTRACK_SMALL("/assets/icons/audiotrack_18dp.png"),
    DOWNLOAD_SMALL("/assets/icons/download_icon_18dp.png"),
    FILTER_LIST_SMALL("/assets/icons/filter_list_18dp.png"),
    HISTORY_SMALL("/assets/icons/history_icon_18dp.png"),
    SEARCH_SMALL("/assets/icons/search_icon_18dp.png");

    private final Image image;

    Icon(String path) {
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public Image getImage() {
        return image;
    }
}