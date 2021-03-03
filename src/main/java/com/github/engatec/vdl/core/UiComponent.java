package com.github.engatec.vdl.core;

public enum UiComponent {

    MAIN("/fxml/main.fxml"),
    PREFERENCES_GENERAL("/fxml/preferences/preferences-general.fxml"),
    PREFERENCES_YOUTUBE_DL("/fxml/preferences/preferences-youtubedl.fxml"),
    VIDEO_DOWNLOAD_GRID("/fxml/video-download-grid.fxml"),
    DOWNLOADABLE_ITEMS_COMPONENT("/fxml/downloadable-items-component.fxml");

    private final String fxml;

    UiComponent(String fxml) {
        this.fxml = fxml;
    }

    public String getFxml() {
        return fxml;
    }
}
