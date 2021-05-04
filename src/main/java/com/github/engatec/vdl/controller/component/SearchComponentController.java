package com.github.engatec.vdl.controller.component;

import java.util.List;

import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.component.DownloadableItemComponent;
import com.github.engatec.vdl.worker.service.DownloadableSearchService;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

    @FXML private ProgressBar searchProgressBar;
    @FXML private Label searchProgressLabel;

    @FXML private CheckBox selectAllCheckBox;

    @FXML private VBox contentNode;

    @FXML private Button downloadButton;

    @FXML
    public void initialize() {
        initSearchControl();
        initSearchService();

        searchButton.setOnAction(this::handleSearchButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private void initSearchControl() {
        ReadOnlyBooleanProperty searchInProgress = downloadableSearchService.runningProperty();

        urlTextField.visibleProperty().bind(searchInProgress.not());
        urlTextField.managedProperty().bind(searchInProgress.not());
        searchButton.visibleProperty().bind(searchInProgress.not());
        searchButton.managedProperty().bind(searchInProgress.not());

        searchProgressBar.visibleProperty().bind(searchInProgress);
        searchProgressBar.managedProperty().bind(searchInProgress);
        cancelButton.visibleProperty().bind(searchInProgress);
        cancelButton.managedProperty().bind(searchInProgress);
        searchProgressLabel.visibleProperty().bind(searchInProgress);

        searchProgressBar.progressProperty().bind(downloadableSearchService.progressProperty());
        searchProgressLabel.textProperty().bind(downloadableSearchService.messageProperty());

        urlTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchButtonClick(event);
            }
        });

        downloadButton.setVisible(false);
        selectAllCheckBox.setManaged(false);
        selectAllCheckBox.setVisible(false);
    }

    private void initSearchService() {
        downloadableSearchService.setOnInfoFetchedCallback(this::updateContentPane);

        downloadableSearchService.setOnSucceeded(it -> {
            // List<VideoInfo> downloadables = (List<VideoInfo>) it.getSource().getValue();
        });

        downloadableSearchService.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
            Dialogs.info("video.search.error");
        });
    }

    private void handleCancelButtonClick(ActionEvent event) {
        downloadableSearchService.cancel();
        event.consume();
    }

    private void handleSearchButtonClick(Event event) {
        downloadButton.setVisible(false);
        selectAllCheckBox.setManaged(false);
        selectAllCheckBox.setVisible(false);
        contentNode.getChildren().clear();
        downloadableSearchService.setUrl(urlTextField.getText());
        downloadableSearchService.restart();
        event.consume();
    }

    private void updateContentPane(List<VideoInfo> downloadables, Integer totalItems) {
        if (CollectionUtils.isNotEmpty(downloadables)) {
            downloadButton.setVisible(true);

            if (totalItems > 1) {
                selectAllCheckBox.setManaged(true);
                selectAllCheckBox.setVisible(true);
            }
        }

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
