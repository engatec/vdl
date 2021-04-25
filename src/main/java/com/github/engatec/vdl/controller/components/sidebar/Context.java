package com.github.engatec.vdl.controller.components.sidebar;

import javafx.scene.control.Label;

class Context {

    private final Label searchLabel;
    private final Label downloadsLabel;
    private final Label historyLabel;

    Context(
            Label searchLabel,
            Label downloadsLabel,
            Label historyLabel
    ) {
        this.searchLabel = searchLabel;
        this.downloadsLabel = downloadsLabel;
        this.historyLabel = historyLabel;
    }

    public Label getSearchLabel() {
        return searchLabel;
    }

    public Label getDownloadsLabel() {
        return downloadsLabel;
    }

    public Label getHistoryLabel() {
        return historyLabel;
    }
}
