package com.github.engatec.vdl.ui;

import java.util.Objects;

import javafx.scene.image.Image;

public enum Icon {

    DOWNLOAD_SMALL("/assets/icons/download_icon_18dp.png"),
    HISTORY_SMALL("/assets/icons/history_icon_18dp.png"),
    MORE_HORIZ_SMALL("/assets/icons/more_horiz_18dp.png"),
    SEARCH_SMALL("/assets/icons/search_icon_18dp.png");

    private final Image image;

    Icon(String path) {
        image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(path)));
    }

    public Image getImage() {
        return image;
    }
}
