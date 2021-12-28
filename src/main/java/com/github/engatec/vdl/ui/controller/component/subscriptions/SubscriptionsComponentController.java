package com.github.engatec.vdl.ui.controller.component.subscriptions;

import java.util.List;
import java.util.function.Consumer;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.SubscriptionsManager;
import com.github.engatec.vdl.model.Subscription;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.service.PlaylistDetailsSearchService;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.component.subscriptions.SubscriptionItemComponent;
import com.github.engatec.vdl.ui.controller.component.ComponentController;
import com.github.engatec.vdl.ui.stage.PlaylistContentsStage;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SubscriptionsComponentController extends VBox implements ComponentController {

    private static final Logger LOGGER = LogManager.getLogger(SubscriptionsComponentController.class);

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final SubscriptionsManager subscriptionsManager = ctx.getManager(SubscriptionsManager.class);

    private final Stage stage;
    private final PlaylistDetailsSearchService playlistDetailsSearchService = new PlaylistDetailsSearchService();

    @FXML private Node rootNode;

    @FXML private TextField urlTextField;
    @FXML private Button searchButton;
    @FXML private Button cancelButton;

    @FXML private ProgressBar searchProgressBar;

    @FXML private VBox contentNode;

    public SubscriptionsComponentController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        initSearchControl();
        initSearchService();

        searchButton.setOnAction(this::handleSearchButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);

        subscriptionsManager.getSubscriptionsAsync()
                .thenAccept(subscriptions ->
                        Platform.runLater(() -> displaySubscriptions(subscriptions))
                );
    }

    private void initSearchControl() {
        ReadOnlyBooleanProperty searchInProgress = playlistDetailsSearchService.runningProperty();

        urlTextField.visibleProperty().bind(searchInProgress.not());
        urlTextField.managedProperty().bind(searchInProgress.not());
        searchButton.visibleProperty().bind(searchInProgress.not());
        searchButton.managedProperty().bind(searchInProgress.not());

        searchProgressBar.visibleProperty().bind(searchInProgress);
        searchProgressBar.managedProperty().bind(searchInProgress);
        cancelButton.visibleProperty().bind(searchInProgress);
        cancelButton.managedProperty().bind(searchInProgress);

        searchProgressBar.progressProperty().bind(playlistDetailsSearchService.progressProperty());

        urlTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchButtonClick(event);
            }
        });
    }

    private void initSearchService() {
        playlistDetailsSearchService.setOnSucceeded(it -> {
            var items = (List<VideoInfo>) it.getSource().getValue();
            if (CollectionUtils.isEmpty(items)) {
                Platform.runLater(() -> Dialogs.info("subscriptions.playlist.notfound"));
                return;
            }

            Platform.runLater(() -> new PlaylistContentsStage(urlTextField.getText(), items, subscription -> {
                displaySubscriptions(List.of(subscription));
                subscriptionsManager.updateSubscription(subscription);
            }).modal(stage).showAndWait());
        });

        playlistDetailsSearchService.setOnFailed(it -> {
            String msg = it.getSource().getException().getMessage();
            LOGGER.warn(msg);
            Platform.runLater(() -> Dialogs.exception("subscriptions.playlist.search.error", msg));
        });
    }

    private void handleCancelButtonClick(ActionEvent event) {
        playlistDetailsSearchService.cancel();
        event.consume();
    }

    private void handleSearchButtonClick(Event event) {
        playlistDetailsSearchService.setUrl(urlTextField.getText());
        playlistDetailsSearchService.restart();
        event.consume();
    }

    private void displaySubscriptions(List<Subscription> subscriptions) {
        ObservableList<Node> contentList = contentNode.getChildren();
        for (Subscription item : subscriptions) {
            SubscriptionItemComponentController node = new SubscriptionItemComponent(stage, item, getOnSubscriptionDeleteButtonClickListener()).load();
            contentList.add(node);
        }
    }

    private Consumer<Subscription> getOnSubscriptionDeleteButtonClickListener() {
        return subscription -> {
            subscriptionsManager.unsubscribe(subscription);
            contentNode.getChildren().removeIf(it -> subscription.equals(((SubscriptionItemComponentController) it).getItem()));
        };
    }

    @Override
    public void onBeforeVisible() {
        Platform.runLater(() -> urlTextField.requestFocus());
    }
}
