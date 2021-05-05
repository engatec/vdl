package com.github.engatec.vdl.controller.component.sidebar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SidebarComponentController extends VBox {

    @FXML private Label searchLabel;
    @FXML private Label downloadsLabel;
    @FXML private Label historyLabel;

    @FXML
    public void initialize() {
        Context ctx = new Context(searchLabel, downloadsLabel, historyLabel);
        Initializer.initialize(ctx);
        Binder.bind(ctx);
    }

    public void setOnSearchClickListener(Runnable onSearchClickListener) {
        searchLabel.setOnMouseClicked(event -> {
            onSearchClickListener.run();
        });
    }

    public void setOnDownloadsClickListener(Runnable onSearchClickListener) {
        downloadsLabel.setOnMouseClicked(event -> {
            onSearchClickListener.run();
        });
    }

    public void setOnHistoryClickListener(Runnable onSearchClickListener) {
        historyLabel.setOnMouseClicked(event -> {
            onSearchClickListener.run();
        });
    }
}
