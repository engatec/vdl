package com.github.engatec.vdl.ui.component.controller.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.handler.CopyUrlFromClipboardOnFocusChangeListener;
import com.github.engatec.vdl.handler.textformatter.NewLineOnUrlPasteTextFormatter;
import com.github.engatec.vdl.model.VideoInfo;
import com.github.engatec.vdl.preference.property.misc.MultiSearchConfigProperty;
import com.github.engatec.vdl.service.DownloadableSearchService;
import com.github.engatec.vdl.ui.component.controller.ComponentController;
import com.github.engatec.vdl.ui.component.core.search.DownloadableItemComponent;
import com.github.engatec.vdl.ui.data.CheckBoxGroup;
import com.github.engatec.vdl.ui.helper.Dialogs;
import com.github.engatec.vdl.util.AppUtils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.github.engatec.vdl.core.AppExecutors.Type.COMMON_EXECUTOR;

public class SearchComponentController extends VBox implements ComponentController {

    private static final Logger LOGGER = LogManager.getLogger(SearchComponentController.class);

    private static final int RENDER_PARTITION_SIZE = 10;

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final QueueManager queueManager = ctx.getManager(QueueManager.class);

    private final Stage stage;
    private final DownloadableSearchService downloadableSearchService = new DownloadableSearchService();
    private final BooleanProperty contentUpdating = new SimpleBooleanProperty(false);

    @FXML private Node rootNode;

    @FXML private StackPane singleSearchStackPane;
    @FXML private TextField urlTextField;
    @FXML private ImageView multiSearchImageView;

    @FXML private StackPane multiSearchStackPane;
    @FXML private TextArea urlTextArea;
    @FXML private ImageView singleSearchImageView;

    @FXML private Button searchButton;
    @FXML private Button cancelButton;

    @FXML private ProgressBar searchProgressBar;
    @FXML private Label foundItemsLabel;
    @FXML private Label itemsCountLabel;

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
                downloadButton.setVisible(selectedCount > 0);
            } else {
                downloadsCountLabel.setText(StringUtils.EMPTY);
                downloadButton.setGraphicTextGap(0);
            }
        });

        initSearchControl();
        initSearchService();

        searchButton.setOnAction(this::handleSearchButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
        downloadButton.setOnAction(this::handleDownloadButtonClick);

        initDragAndDrop();
        stage.focusedProperty().addListener(new CopyUrlFromClipboardOnFocusChangeListener(urlTextField, urlTextArea, searchButton));
    }

    private void initSearchControl() {
        BooleanBinding searchInProgress = downloadableSearchService.runningProperty().or(contentUpdating);

        initSingleSearchTextField(searchInProgress);
        initMultiSearchTextArea(searchInProgress);

        searchButton.visibleProperty().bind(searchInProgress.not());
        searchButton.managedProperty().bind(searchInProgress.not());
        searchProgressBar.visibleProperty().bind(searchInProgress);
        searchProgressBar.managedProperty().bind(searchInProgress);
        cancelButton.visibleProperty().bind(searchInProgress);
        cancelButton.managedProperty().bind(searchInProgress);

        searchProgressBar.setProgress(-1);
        foundItemsLabel.visibleProperty().bind(itemsCountLabel.visibleProperty());
        itemsCountLabel.visibleProperty().bind(Bindings.createBooleanBinding(() -> StringUtils.isNotBlank(itemsCountLabel.getText()), itemsCountLabel.textProperty()));
        itemsCountLabel.setText(null);

        selectAllCheckBox.setManaged(false);
        selectAllCheckBox.setVisible(false);

        downloadButton.setVisible(false);
        MenuItem downloadAudioMenuItem = new MenuItem(ctx.getLocalizedString("download.audio"));
        downloadAudioMenuItem.setOnAction(this::handleDownloadAudioButtonClick);
        downloadButton.getItems().add(downloadAudioMenuItem);
    }

    private void initSingleSearchTextField(BooleanBinding searchInProgress) {
        var multiSearchActive = ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getProperty();
        var singleSearchActive = multiSearchActive.not();

        singleSearchStackPane.visibleProperty().bind(searchInProgress.not().and(singleSearchActive));
        singleSearchStackPane.managedProperty().bind(searchInProgress.not().and(singleSearchActive));

        multiSearchImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> {
            double urlTextFieldHeight = urlTextField.getHeight();
            return urlTextFieldHeight - urlTextFieldHeight * 0.4;
        }, urlTextField.heightProperty()));
        multiSearchImageView.fitWidthProperty().bind(multiSearchImageView.fitHeightProperty());
        multiSearchImageView.setOnMouseClicked(event -> {
            multiSearchActive.setValue(true);
            urlTextField.setText(StringUtils.EMPTY);
            Platform.runLater(() -> urlTextArea.requestFocus());
            event.consume();
        });

        urlTextField.paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(4, 14 + multiSearchImageView.getFitWidth(), 4, 7), multiSearchImageView.fitWidthProperty()));
        urlTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchButtonClick(event);
            }
        });
    }

    private void initMultiSearchTextArea(BooleanBinding searchInProgress) {
        var multiSearchActive = ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getProperty();

        multiSearchStackPane.visibleProperty().bind(searchInProgress.not().and(multiSearchActive));
        multiSearchStackPane.managedProperty().bind(searchInProgress.not().and(multiSearchActive));

        singleSearchImageView.fitHeightProperty().bind(multiSearchImageView.fitHeightProperty());
        singleSearchImageView.fitWidthProperty().bind(multiSearchImageView.fitWidthProperty());
        singleSearchImageView.setOnMouseClicked(event -> {
            multiSearchActive.setValue(false);
            urlTextArea.setText(StringUtils.EMPTY);
            Platform.runLater(() -> urlTextField.requestFocus());
            event.consume();
        });

        urlTextArea.setTextFormatter(new NewLineOnUrlPasteTextFormatter());

        // A hack to calculate singleSearchImageView margin when textarea scroll becomes visible/invisible
        Platform.runLater(() -> { // Must wrap in Platform.runLater to have .scroll-bar initialized
            for (Node node : urlTextArea.lookupAll(".scroll-bar")) {
                if (node instanceof ScrollBar scrollBar && scrollBar.getOrientation() == Orientation.VERTICAL) {
                    scrollBar.visibleProperty().addListener((observable, oldValue, newValue) ->
                        Platform.runLater(() -> { // Must wrap in Platform.runLater to have scrollBar.getWidth() correctly calculated
                            double rightMargin = 4;
                            if (newValue) {
                                rightMargin += scrollBar.getWidth();
                            }
                            StackPane.setMargin(singleSearchImageView, new Insets(0, rightMargin, 4, 0));
                        })
                    );
                }
            }
        });
    }

    private void initSearchService() {
        downloadableSearchService.setOnSucceeded(it -> {
            var items = (List<VideoInfo>) it.getSource().getValue();
            if (CollectionUtils.isNotEmpty(items)) {
                updateContentPane(items);
            } else {
                Platform.runLater(() -> Dialogs.info("video.search.notfound"));
            }
        });

        downloadableSearchService.setOnFailed(it -> {
            String msg = it.getSource().getException().getMessage();
            LOGGER.warn(msg);
            Platform.runLater(() -> Dialogs.exception("video.search.notfound", msg));
        });
    }

    private void handleCancelButtonClick(ActionEvent event) {
        downloadableSearchService.cancel();
        contentUpdating.set(false);
        event.consume();
    }

    private void handleSearchButtonClick(Event event) {
        String urlString = StringUtils.firstNonBlank(urlTextField.getText(), urlTextArea.getText());
        if (StringUtils.isNotBlank(urlString)) {
            List<String> urls = Arrays.stream(urlString.split("\\s"))
                    .map(StringUtils::strip)
                    .filter(StringUtils::isNotBlank)
                    .toList();

            clearSearchPane(false);

            var singleSearchActive = !ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getValue();
            if (singleSearchActive && isUrlBeingDownloaded(urls.stream().findFirst().orElse(null))) {
                Dialogs.infoWithYesNoButtons("stage.main.search.dialog.suchitemisbeingdownloaded", () -> startSearch(urls), null);
            } else {
                startSearch(urls);
            }
        }

        event.consume();
    }

    private void startSearch(List<String> urls) {
        List<String> normalizedUrls = urls.stream()
                .map(AppUtils::normalizeBaseUrl)
                .toList();
        downloadableSearchService.setUrls(normalizedUrls);
        downloadableSearchService.restart();
    }

    private boolean isUrlBeingDownloaded(String url) {
        return queueManager.hasItem(it -> StringUtils.equals(it.getBaseUrl(), url));
    }

    private void updateContentPane(List<VideoInfo> downloadables) {
        contentUpdating.set(true);
        downloadButton.setVisible(true);

        int totalItems = downloadables.size();
        if (totalItems > 1) {
            itemsCountLabel.setText(String.valueOf(totalItems));
            selectAllCheckBox.setManaged(true);
            selectAllCheckBox.setVisible(true);
            selectAllCheckBox.setSelected(true);
        }

        double progressStep = (double) RENDER_PARTITION_SIZE / totalItems;

        CompletableFuture.runAsync(() -> {
                    for (List<VideoInfo> partition : ListUtils.partition(downloadables, RENDER_PARTITION_SIZE)) {
                        if (!contentUpdating.get()) {
                            break;
                        }

                        final List<DownloadableItemComponentController> controllers = new ArrayList<>();
                        for (VideoInfo downloadable : partition) {
                            DownloadableItemComponentController controller = new DownloadableItemComponent(stage, downloadable).load();
                            controller.setSelectable(totalItems > 1);
                            controllers.add(controller);
                        }

                        Platform.runLater(() -> {
                            for (DownloadableItemComponentController it : controllers) {
                                ObservableList<Node> contentItems = contentNode.getChildren();
                                if (CollectionUtils.isNotEmpty(contentItems)) {
                                    contentItems.add(new Separator());
                                }
                                it.setSelected(selectAllCheckBox.isSelected());
                                checkBoxGroup.add(it.getItemSelectedCheckBox());
                                contentItems.add(it);
                            }

                            searchProgressBar.setProgress(Math.max(searchProgressBar.getProgress(), 0) + progressStep);
                        });
                    }
                }, ctx.appExecutors().get(COMMON_EXECUTOR)
        ).thenRun(() -> Platform.runLater(() -> {
            contentUpdating.set(false);
            searchProgressBar.setProgress(-1);
        }));
    }

    private List<DownloadableItemComponentController> getSelectedItems() {
        return contentNode.getChildren().stream()
                .filter(it -> it instanceof DownloadableItemComponentController)
                .map(it -> (DownloadableItemComponentController) it)
                .filter(DownloadableItemComponentController::isSelected)
                .collect(Collectors.toList());
    }

    private void handleDownloadButtonClick(ActionEvent e) {
        AppUtils.resolveDownloadPath(stage).ifPresent(path -> {
            getSelectedItems().forEach(it -> it.download(path));
            clearSearchPane(true);
        });
        e.consume();
    }

    private void handleDownloadAudioButtonClick(ActionEvent e) {
        AppUtils.resolveDownloadPath(stage).ifPresent(path -> {
            getSelectedItems().forEach(it -> it.downloadAudio(path));
            clearSearchPane(true);
        });
        e.consume();
    }

    private void clearSearchPane(boolean clearUrlTextField) {
        if (clearUrlTextField) {
            urlTextField.clear();
            urlTextArea.clear();
        }
        downloadButton.setVisible(false);
        selectAllCheckBox.setManaged(false);
        selectAllCheckBox.setVisible(false);
        itemsCountLabel.setText(null);
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
                Boolean multiSearchActive = ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getValue();
                if (multiSearchActive) {
                    urlTextArea.appendText(dragboard.getString() + System.lineSeparator());
                } else {
                    urlTextField.setText(dragboard.getString());
                    searchButton.fire();
                }

                e.setDropCompleted(true);
            }
            e.consume();
        });
    }

    @Override
    public void onBeforeVisible() {
        Platform.runLater(() -> {
            Boolean multiSearchActive = ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getValue();
            if (multiSearchActive) {
                urlTextArea.requestFocus();
            } else {
                urlTextField.requestFocus();
            }
        });
    }
}
