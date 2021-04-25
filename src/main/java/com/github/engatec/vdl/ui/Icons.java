package com.github.engatec.vdl.ui;

import java.util.Objects;

import javafx.scene.image.Image;

public class Icons {

    public static final Image DOWNLOAD_SMALL = loadIcon("/assets/icons/download_icon_18dp.png");
    public static final Image HISTORY_SMALL = loadIcon("/assets/icons/history_icon_18dp.png");
    public static final Image SEARCH_SMALL = loadIcon("/assets/icons/search_icon_18dp.png");

    private static Image loadIcon(String path) {
        return new Image(Objects.requireNonNull(Icons.class.getResourceAsStream(path)));
    }
}
