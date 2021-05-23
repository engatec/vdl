package com.github.engatec.vdl.controller.component.subscriptions;

import java.util.List;

import com.github.engatec.vdl.controller.component.ComponentController;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.stage.subscriptions.PlaylistContentsStage;
import com.github.engatec.vdl.worker.service.PlaylistDetailsSearchService;
import javafx.beans.property.ReadOnlyBooleanProperty;
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
                Dialogs.info("subscriptions.playlist.notfound");
                return;
            }

            new PlaylistContentsStage(items).modal(stage).showAndWait();
        });

        playlistDetailsSearchService.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage());
            Dialogs.info("subscriptions.playlist.error");
        });
    }

    private void handleCancelButtonClick(ActionEvent event) {
        playlistDetailsSearchService.cancel();
        event.consume();
    }

    private void handleSearchButtonClick(Event event) {
        contentNode.getChildren().clear();
        playlistDetailsSearchService.setUrl(urlTextField.getText());
        playlistDetailsSearchService.restart();
        event.consume();
    }
}
