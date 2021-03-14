package com.github.engatec.vdl.core;

public enum UiComponent {

    MAIN("/fxml/main.fxml");

    private final String fxml;

    UiComponent(String fxml) {
        this.fxml = fxml;
    }

    public String getFxml() {
        return fxml;
    }
}
