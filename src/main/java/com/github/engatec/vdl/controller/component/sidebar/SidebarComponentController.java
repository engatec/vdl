package com.github.engatec.vdl.controller.component.sidebar;

import java.util.function.Consumer;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

public class SidebarComponentController extends VBox {

    @FXML private Label searchLabel;
    @FXML private Label downloadsLabel;
    @FXML private Label queueItemsCountLabel;
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
            event.consume();
        });
    }

    public void setOnDownloadsClickListener(Runnable onSearchClickListener) {
        downloadsLabel.setOnMouseClicked(event -> {
            onSearchClickListener.run();
            event.consume();
        });
    }

    public void setOnHistoryClickListener(Runnable onSearchClickListener) {
        historyLabel.setOnMouseClicked(event -> {
            onSearchClickListener.run();
            event.consume();
        });
    }

    public Consumer<Integer> getOnQueueItemsChangeListener() {
        return count -> queueItemsCountLabel.setText(count == 0 ? StringUtils.EMPTY : "(" + count + ")");
    }
}
