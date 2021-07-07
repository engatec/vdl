package com.github.engatec.vdl.controller.component.search;

import java.util.List;
import java.util.stream.Collectors;

import com.github.engatec.vdl.controller.component.ComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.handler.CopyUrlFromClipboardOnFocusChangeListener;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.ui.CheckBoxGroup;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.component.search.DownloadableItemComponent;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.worker.service.DownloadableSearchService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchComponentController extends VBox implements ComponentController {

    private static final Logger LOGGER = LogManager.getLogger(SearchComponentController.class);

    private final Stage stage;
    private final DownloadableSearchService downloadableSearchService = new DownloadableSearchService();

    @FXML private Node rootNode;

    @FXML private TextField urlTextField;
    @FXML private Button searchButton;
    @FXML private Button cancelButton;

    @FXML private ProgressBar searchProgressBar;
    @FXML private Label searchProgressLabel;

    @FXML private CheckBox selectAllCheckBox;
    private CheckBoxGroup checkBoxGroup;

    @FXML private VBox contentNode;

    @FXML private SplitMenuButton downloadButton;
    @FXML private Label downloadsCountLabel;

    public SearchComponentController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        checkBoxGroup = new CheckBoxGroup(selectAllCheckBox);
        checkBoxGroup.setOnSelectionUpdateListener(selectedCount -> {
            // FIXME: downloadsCountLabel visibility workaround due to a bug in JavaFX managed property (label becomes managed only after parent button re-renders)
            if (selectAllCheckBox.isVisible()) {
                downloadsCountLabel.setText("(" + selectedCount + ")");
                downloadButton.setGraphicTextGap(2);
            } else {
                downloadsCountLabel.setText(StringUtils.EMPTY);
                downloadButton.setGraphicTextGap(0);
            }
            downloadButton.setVisible(selectedCount > 0);
        });

        initSearchControl();
        initSearchService();

        searchButton.setOnAction(this::handleSearchButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
        downloadButton.setOnAction(this::handleDownloadButtonClick);

        initDragAndDrop();
        stage.focusedProperty().addListener(new CopyUrlFromClipboardOnFocusChangeListener(urlTextField, searchButton));
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

        selectAllCheckBox.setManaged(false);
        selectAllCheckBox.setVisible(false);

        downloadButton.setVisible(false);
        MenuItem downloadAudioMenuItem = new MenuItem(ApplicationContext.INSTANCE.getResourceBundle().getString("download.audio"));
        downloadAudioMenuItem.setOnAction(this::handleDownloadAudioButtonClick);
        downloadButton.getItems().add(downloadAudioMenuItem);
    }

    private void initSearchService() {
        downloadableSearchService.setOnInfoFetchedCallback(this::updateContentPane);

        downloadableSearchService.setOnSucceeded(it -> {
            var items = (List<VideoInfo>) it.getSource().getValue();
            if (CollectionUtils.isEmpty(items)) {
                Platform.runLater(() -> Dialogs.info("video.search.notfound"));
            }
        });

        downloadableSearchService.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage());
            Dialogs.info("video.search.error");
        });
    }

    private void handleCancelButtonClick(ActionEvent event) {
        downloadableSearchService.cancel();
        event.consume();
    }

    private void handleSearchButtonClick(Event event) {
        String url = urlTextField.getText();
        clearSearchPane(false);
        if (isUrlBeingDownloaded(url)) {
            Dialogs.infoWithYesNoButtons("stage.main.search.dialog.suchitemisbeingdownloaded", () -> startSearch(url), null);
        } else {
            startSearch(url);
        }
        event.consume();
    }

    private void startSearch(String url) {
        String normUrl = AppUtils.normalizeBaseUrl(url);
        downloadableSearchService.setUrl(normUrl);
        downloadableSearchService.restart();
    }

    private boolean isUrlBeingDownloaded(String url) {
        return QueueManager.INSTANCE.hasItem(it -> StringUtils.equals(it.getBaseUrl(), url));
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
            ObservableList<Node> contentItems = contentNode.getChildren();
            if (CollectionUtils.isNotEmpty(contentItems)) {
                contentItems.add(new Separator());
            }
            DownloadableItemComponentController controller = new DownloadableItemComponent(stage, downloadable).load();
            controller.setSelectable(totalItems > 1);
            controller.setSelected(true);
            checkBoxGroup.add(controller.getItemSelectedCheckBox());
            contentItems.add(controller);
        }
    }

    private List<DownloadableItemComponentController> getSelectedItems() {
        return contentNode.getChildren().stream()
                .filter(it -> it instanceof DownloadableItemComponentController)
                .map(it -> (DownloadableItemComponentController) it)
                .filter(DownloadableItemComponentController::isSelected)
                .collect(Collectors.toList());
    }

    private void handleDownloadButtonClick(ActionEvent e) {
        AppUtils.resolveDownloadPath(stage).ifPresent(path -> getSelectedItems().forEach(it -> it.download(path)));
        clearSearchPane(true);
        e.consume();
    }

    private void handleDownloadAudioButtonClick(ActionEvent e) {
        AppUtils.resolveDownloadPath(stage).ifPresent(path -> getSelectedItems().forEach(it -> it.downloadAudio(path)));
        clearSearchPane(true);
        e.consume();
    }

    private void clearSearchPane(boolean clearUrlTextField) {
        if (clearUrlTextField) {
            urlTextField.clear();
        }
        downloadButton.setVisible(false);
        selectAllCheckBox.setManaged(false);
        selectAllCheckBox.setVisible(false);
        contentNode.getChildren().clear();
        checkBoxGroup.clear();
    }

    private void initDragAndDrop() {
        rootNode.setOnDragOver(e -> {
            if (searchButton.isVisible() && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        rootNode.setOnDragDropped(e -> {
            Dragboard dragboard = e.getDragboard();
            if (searchButton.isVisible() && e.getTransferMode() == TransferMode.COPY && dragboard.hasString()) {
                urlTextField.setText(dragboard.getString());
                searchButton.fire();
                e.setDropCompleted(true);
            }
            e.consume();
        });
    }
}
