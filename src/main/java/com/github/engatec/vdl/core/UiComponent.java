package com.github.engatec.vdl.core;

public enum UiComponent {

    MAIN("/fxml/main.fxml"),
    PREFERENCES("/fxml/preferences.fxml"),
    VIDEO_DOWNLOAD_GRID("/fxml/video-download-grid.fxml"),
    AUTIO_DOWNLOAD_GRID("/fxml/audio-download-grid.fxml"),
    DOWNLOADING_PROGRESS("/fxml/downloading-progress.fxml");

    private final String fxml;

    UiComponent(String fxml) {
        this.fxml = fxml;
    }

    public String getFxml() {
        return fxml;
    }
}
