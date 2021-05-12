package com.github.engatec.vdl.controller.component;

import java.util.function.Consumer;

import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.ui.Icon;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;

public class SidebarComponentController extends VBox {

    @FXML private Label searchLabel;
    @FXML private HBox downloadsLabelWrapperNode;
    @FXML private Label downloadsLabel;
    @FXML private Label queueItemsCountLabel;
    @FXML private Label historyLabel;
    @FXML private HBox sidebarWidthCorrectionNode; // Dummy node to prevent the sidebar from changing width on queueItemsCountLabel text change
    @FXML private Label dummyWidthLabel;

    @FXML
    public void initialize() {
        sidebarWidthCorrectionNode.setVisible(false);
        initGraphic();
        bindLocale();
    }

    private void initGraphic() {
        searchLabel.setGraphic(new ImageView(Icon.SEARCH_SMALL.getImage()));
        downloadsLabel.setGraphic(new ImageView(Icon.DOWNLOAD_SMALL.getImage()));
        historyLabel.setGraphic(new ImageView(Icon.HISTORY_SMALL.getImage()));
        dummyWidthLabel.setGraphic(new ImageView(Icon.DOWNLOAD_SMALL.getImage()));
    }

    private void bindLocale() {
        I18n.bindLocaleProperty(searchLabel.textProperty(), "sidebar.search");
        I18n.bindLocaleProperty(downloadsLabel.textProperty(), "sidebar.downloads");
        I18n.bindLocaleProperty(historyLabel.textProperty(), "sidebar.history");
        I18n.bindLocaleProperty(dummyWidthLabel.textProperty(), "sidebar.downloads");
    }

    public void setOnSearchClickListener(Runnable onSearchClickListener) {
        searchLabel.setOnMouseClicked(event -> {
            onSearchClickListener.run();
            event.consume();
        });
    }

    public void setOnDownloadsClickListener(Runnable onSearchClickListener) {
        downloadsLabelWrapperNode.setOnMouseClicked(event -> {
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
