package com.github.engatec.vdl.controller.component;

import java.util.Set;
import java.util.function.Consumer;

import com.github.engatec.vdl.ui.Icon;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
    @FXML private Label subscriptionsLabel;
    @FXML private ProgressIndicator subscriptionsUpdateProgressIndicator;
    @FXML private HBox sidebarWidthCorrectionNode; // Dummy node to prevent the sidebar from changing width on queueItemsCountLabel text change
    @FXML private Label dummyWidthLabel;

    private Set<Node> clickableLabels;

    @FXML
    public void initialize() {
        sidebarWidthCorrectionNode.setVisible(false);
        initGraphic();
        initSubscriptionsUpdateProgressIndicator();

        clickableLabels = Set.of(searchLabel, downloadsLabelWrapperNode, historyLabel, subscriptionsLabel);
        setLabelCurrent(searchLabel);
    }

    private void initSubscriptionsUpdateProgressIndicator() {
        subscriptionsUpdateProgressIndicator.setVisible(false);
        subscriptionsUpdateProgressIndicator.prefWidthProperty().bind(subscriptionsLabel.heightProperty());
        subscriptionsUpdateProgressIndicator.prefHeightProperty().bind(subscriptionsLabel.heightProperty());
    }

    private void initGraphic() {
        searchLabel.setGraphic(new ImageView(Icon.SEARCH_SMALL.getImage()));
        downloadsLabel.setGraphic(new ImageView(Icon.DOWNLOAD_SMALL.getImage()));
        historyLabel.setGraphic(new ImageView(Icon.HISTORY_SMALL.getImage()));
        subscriptionsLabel.setGraphic(new ImageView(Icon.SUBSCRIPTIONS_SMALL.getImage()));
        dummyWidthLabel.setGraphic(new ImageView(Icon.DOWNLOAD_SMALL.getImage()));
    }

    public void setOnSearchClickListener(Runnable listener) {
        searchLabel.setOnMouseClicked(event -> {
            setLabelCurrent(searchLabel);
            listener.run();
            event.consume();
        });
    }

    public void setOnDownloadsClickListener(Runnable listener) {
        downloadsLabelWrapperNode.setOnMouseClicked(event -> {
            setLabelCurrent(downloadsLabelWrapperNode);
            listener.run();
            event.consume();
        });
    }

    public void setOnHistoryClickListener(Runnable listener) {
        historyLabel.setOnMouseClicked(event -> {
            setLabelCurrent(historyLabel);
            listener.run();
            event.consume();
        });
    }

    public void setOnSubscruptionsClickListener(Runnable listener) {
        subscriptionsLabel.setOnMouseClicked(event -> {
            setLabelCurrent(subscriptionsLabel);
            listener.run();
            event.consume();
        });
    }

    private void setLabelCurrent(Node label) {
        String styleClass = "sidebar-label-current";
        for (Node node : clickableLabels) {
            node.getStyleClass().remove(styleClass);
        }
        label.getStyleClass().add(styleClass);
    }

    public Consumer<Integer> getOnQueueItemsChangeListener() {
        return count -> queueItemsCountLabel.setText(count == 0 ? StringUtils.EMPTY : "(" + count + ")");
    }

    public Consumer<Boolean> getSubscriptionsUpdateProgressListener() {
        return inProgress -> subscriptionsUpdateProgressIndicator.setVisible(inProgress);
    }
}
