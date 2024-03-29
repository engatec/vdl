package com.github.engatec.vdl.ui.component.controller.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.QueueManager;
import com.github.engatec.vdl.exception.ServiceStubException;
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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

    private enum ContentState {
        EMPTY, SEARCHING, NOT_FOUND, RENDERING, SHOWING
    }

    private static final int RENDER_PARTITION_SIZE = 10;

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final QueueManager queueManager = ctx.getManager(QueueManager.class);

    private final Stage stage;
    private final DownloadableSearchService downloadableSearchService = new DownloadableSearchService();
    private final ObjectProperty<ContentState> contentStateProperty = new SimpleObjectProperty<>();

    @FXML private Node rootNode;

    @FXML private HBox searchPane;
    @FXML private HBox progressPane;
    @FXML private HBox searchMetadataPane;
    @FXML private VBox contentPane;
    @FXML private AnchorPane actionButtonPane;

    @FXML private StackPane singleSearchStackPane;
    @FXML private TextField urlTextField;
    @FXML private ImageView searchExpandImageView;

    @FXML private StackPane multiSearchStackPane;
    @FXML private TextArea urlTextArea;
    @FXML private ImageView searchCollapseImageView;

    @FXML private Button searchButton;
    @FXML private Button cancelButton;

    @FXML private ProgressBar searchProgressBar;

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
        initSearchPane();
        initProgressPane();
        initSearchMetadataPane();
        initContentPane();
        initActionButtonPane();
        initDragAndDrop();
        initSearchService();
        initStates();

        stage.focusedProperty().addListener(new CopyUrlFromClipboardOnFocusChangeListener(urlTextField, urlTextArea, searchButton));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initSearchPane() {
        searchPane.managedProperty().bind(searchPane.visibleProperty());

        initSingleSearchField();
        initMultiSearchField();

        searchButton.visibleProperty().bind(searchPane.visibleProperty());
        searchButton.setOnAction(this::handleSearchButtonClick);
    }

    private void initSingleSearchField() {
        var multiSearchActive = ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getProperty();
        var singleSearchActive = multiSearchActive.not();

        singleSearchStackPane.visibleProperty().bind(singleSearchActive);
        singleSearchStackPane.managedProperty().bind(singleSearchStackPane.visibleProperty());

        searchExpandImageView.fitHeightProperty().bind(Bindings.createDoubleBinding(() -> {
            double urlTextFieldHeight = urlTextField.getHeight();
            return urlTextFieldHeight - urlTextFieldHeight * 0.4;
        }, urlTextField.heightProperty()));
        searchExpandImageView.fitWidthProperty().bind(searchExpandImageView.fitHeightProperty());
        searchExpandImageView.setOnMouseClicked(event -> {
            multiSearchActive.setValue(true);
            urlTextField.setText(StringUtils.EMPTY);
            Platform.runLater(() -> urlTextArea.requestFocus());
            event.consume();
        });

        urlTextField.paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(4, 14 + searchExpandImageView.getFitWidth(), 4, 7), searchExpandImageView.fitWidthProperty()));
        urlTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchButtonClick(event);
            }
        });
    }

    private void initMultiSearchField() {
        var multiSearchActive = ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getProperty();

        multiSearchStackPane.visibleProperty().bind(multiSearchActive);
        multiSearchStackPane.managedProperty().bind(multiSearchStackPane.visibleProperty());

        searchCollapseImageView.fitHeightProperty().bind(searchExpandImageView.fitHeightProperty());
        searchCollapseImageView.fitWidthProperty().bind(searchExpandImageView.fitWidthProperty());
        searchCollapseImageView.setOnMouseClicked(event -> {
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
                                StackPane.setMargin(searchCollapseImageView, new Insets(0, rightMargin, 4, 0));
                            })
                    );
                }
            }
        });
    }

    private void handleSearchButtonClick(Event event) {
        String urlString = StringUtils.firstNonBlank(urlTextField.getText(), urlTextArea.getText());
        if (StringUtils.isNotBlank(urlString)) {
            contentStateProperty.set(ContentState.SEARCHING);

            List<String> urls = Arrays.stream(urlString.split("\\s"))
                    .map(StringUtils::strip)
                    .filter(StringUtils::isNotBlank)
                    .toList();

            var singleSearchActive = !ctx.getConfigRegistry().get(MultiSearchConfigProperty.class).getValue();
            if (singleSearchActive && isUrlBeingDownloaded(urls.stream().findFirst().orElse(null))) {
                Dialogs.infoWithYesNoButtons(
                        "stage.main.search.dialog.suchitemisbeingdownloaded",
                        () -> startSearch(urls),
                        () -> contentStateProperty.set(ContentState.EMPTY)
                );
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

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initProgressPane() {
        progressPane.managedProperty().bind(progressPane.visibleProperty());
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private void handleCancelButtonClick(ActionEvent event) {
        downloadableSearchService.cancel();
        if (CollectionUtils.isEmpty(contentNode.getChildren())) {
            contentStateProperty.set(ContentState.NOT_FOUND);
        } else {
            contentStateProperty.set(ContentState.SHOWING);
        }
        event.consume();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initSearchMetadataPane() {
        searchMetadataPane.visibleProperty().bind(Bindings.createBooleanBinding(() -> StringUtils.isNotBlank(itemsCountLabel.getText()), itemsCountLabel.textProperty()));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initContentPane() {
        initCheckboxes();
    }

    private void initCheckboxes() {
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
        selectAllCheckBox.managedProperty().bind(selectAllCheckBox.visibleProperty());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initActionButtonPane() {
        downloadButton.setOnAction(this::handleDownloadButtonClick);

        MenuItem downloadAudioMenuItem = new MenuItem(ctx.getLocalizedString("download.audio"));
        downloadAudioMenuItem.setOnAction(this::handleDownloadAudioButtonClick);
        downloadButton.getItems().add(downloadAudioMenuItem);
    }

    private void handleDownloadButtonClick(ActionEvent e) {
        AppUtils.resolveDownloadPath(stage).ifPresent(path -> {
            getSelectedItems().forEach(it -> it.download(path));
            contentStateProperty.set(ContentState.EMPTY);
        });
        e.consume();
    }

    private void handleDownloadAudioButtonClick(ActionEvent e) {
        AppUtils.resolveDownloadPath(stage).ifPresent(path -> {
            getSelectedItems().forEach(it -> it.downloadAudio(path));
            contentStateProperty.set(ContentState.EMPTY);
        });
        e.consume();
    }

    private List<DownloadableItemComponentController> getSelectedItems() {
        return contentNode.getChildren().stream()
                .filter(it -> it instanceof DownloadableItemComponentController)
                .map(it -> (DownloadableItemComponentController) it)
                .filter(it -> !selectAllCheckBox.isVisible() || it.isSelected())
                .collect(Collectors.toList());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initSearchService() {
        downloadableSearchService.setOnSucceeded(it -> {
            var items = (List<VideoInfo>) it.getSource().getValue();
            if (CollectionUtils.isNotEmpty(items)) {
                updateContentPane(items);
            } else {
                Platform.runLater(() -> {
                    String errMsg = it.getSource().getMessage();
                    if (StringUtils.isBlank(errMsg)) {
                        Dialogs.info("video.search.notfound");
                    } else {
                        Dialogs.exception("video.search.notfound", errMsg);
                    }
                });
                contentStateProperty.set(ContentState.NOT_FOUND);
            }
        });

        downloadableSearchService.setOnFailed(it -> {
            Throwable exception = Objects.requireNonNullElseGet(it.getSource().getException(), () -> new ServiceStubException(downloadableSearchService.getClass()));
            String msg = exception.getMessage();
            LOGGER.warn(msg, exception);
            Platform.runLater(() -> Dialogs.exception("video.search.notfound", msg));
            contentStateProperty.set(ContentState.NOT_FOUND);
        });
    }

    private void updateContentPane(List<VideoInfo> downloadables) {
        contentStateProperty.set(ContentState.RENDERING);

        int totalItems = downloadables.size();
        if (totalItems > 1) {
            itemsCountLabel.setText(String.valueOf(totalItems));
            selectAllCheckBox.setVisible(true);
            selectAllCheckBox.setSelected(true);
        }

        double progressStep = (double) RENDER_PARTITION_SIZE / totalItems;

        CompletableFuture.runAsync(() -> {
                    for (List<VideoInfo> partition : ListUtils.partition(downloadables, RENDER_PARTITION_SIZE)) {
                        if (contentStateProperty.get() != ContentState.RENDERING) {
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
        ).thenRun(() -> Platform.runLater(() -> contentStateProperty.set(ContentState.SHOWING)));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initStates() {
        var emptyState = Bindings.createBooleanBinding(() -> contentStateProperty.get() == ContentState.EMPTY, contentStateProperty);
        var searchingState = Bindings.createBooleanBinding(() -> contentStateProperty.get() == ContentState.SEARCHING, contentStateProperty);
        var notFoundState = Bindings.createBooleanBinding(() -> contentStateProperty.get() == ContentState.NOT_FOUND, contentStateProperty);
        var renderingState = Bindings.createBooleanBinding(() -> contentStateProperty.get() == ContentState.RENDERING, contentStateProperty);
        var showingState = Bindings.createBooleanBinding(() -> contentStateProperty.get() == ContentState.SHOWING, contentStateProperty);

        searchPane.visibleProperty().bind(emptyState.or(notFoundState).or(showingState));
        progressPane.visibleProperty().bind(searchingState.or(renderingState));
        contentPane.visibleProperty().bind(renderingState.or(showingState));
        actionButtonPane.visibleProperty().bind(renderingState.or(showingState));

        contentStateProperty.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case EMPTY -> {
                    itemsCountLabel.setText(null);
                    urlTextField.clear();
                    urlTextArea.clear();
                    selectAllCheckBox.setVisible(false);
                    contentNode.getChildren().clear();
                    checkBoxGroup.clear();
                }
                case SEARCHING -> {
                    searchProgressBar.setProgress(-1);
                    itemsCountLabel.setText(null);
                    selectAllCheckBox.setVisible(false);
                    contentNode.getChildren().clear();
                    checkBoxGroup.clear();
                }
            }
        });
        contentStateProperty.set(ContentState.EMPTY);
    }

    private void initDragAndDrop() {
        rootNode.setOnDragOver(e -> {
            if (searchPane.isVisible() && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        rootNode.setOnDragDropped(e -> {
            Dragboard dragboard = e.getDragboard();
            if (searchPane.isVisible() && e.getTransferMode() == TransferMode.COPY && dragboard.hasString()) {
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
