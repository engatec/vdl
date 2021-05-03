package com.github.engatec.vdl.controller.component;

import java.util.List;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.component.DownloadableItemComponent;
import com.github.engatec.vdl.worker.service.DownloadableSearchService;
import javafx.beans.property.BooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchComponentController extends VBox {

    private static final Logger LOGGER = LogManager.getLogger(SearchComponentController.class);

    private final DownloadableSearchService downloadableSearchService = new DownloadableSearchService();

    @FXML private TextField urlTextField;
    @FXML private Button searchButton;
    @FXML private Button cancelButton;

    @FXML private HBox searchProgressWrapperNode;
    @FXML private ProgressBar searchProgressBar;
    @FXML private Label searchProgressLabel;

    @FXML private VBox contentNode;

    @FXML
    public void initialize() {
        initSearchControl();

        searchButton.setOnAction(this::handleSearchButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private void initSearchControl() {
        BooleanProperty searchRunningProperty = cancelButton.visibleProperty();
        searchRunningProperty.set(false);

        searchButton.managedProperty().bind(searchRunningProperty.not());
        searchButton.visibleProperty().bind(searchRunningProperty.not());
        cancelButton.managedProperty().bind(searchRunningProperty);

        searchProgressWrapperNode.visibleProperty().bind(searchRunningProperty);

        searchRunningProperty.bind(downloadableSearchService.runningProperty());
        searchProgressBar.progressProperty().bind(downloadableSearchService.progressProperty());
        searchProgressLabel.textProperty().bind(downloadableSearchService.messageProperty());

        urlTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchButtonClick(event);
            }
        });
    }

    private void handleCancelButtonClick(ActionEvent event) {
        downloadableSearchService.cancel();
        event.consume();
    }

    private void handleSearchButtonClick(Event event) {
        searchDownloadables();
        event.consume();
    }

    private void searchDownloadables() {
        downloadableSearchService.setUrl(urlTextField.getText());
        downloadableSearchService.setOnInfoFetchedCallback(this::updateContentPane);

        downloadableSearchService.setOnSucceeded(it -> {
            // List<VideoInfo> downloadables = (List<VideoInfo>) it.getSource().getValue();
        });

        downloadableSearchService.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
            Dialogs.info("video.search.error");
        });

        downloadableSearchService.restart();
    }

    private void updateContentPane(List<VideoInfo> downloadables, Integer totalItems) {
        for (VideoInfo downloadable : downloadables) {
            DownloadableItemComponentController load = new DownloadableItemComponent((Stage) urlTextField.getScene().getWindow(), downloadable).load();
            ObservableList<Node> contentItems = contentNode.getChildren();
            if (CollectionUtils.isNotEmpty(contentItems)) {
                contentItems.add(new Separator());
            }
            contentItems.add(load);
        }
    }
}
