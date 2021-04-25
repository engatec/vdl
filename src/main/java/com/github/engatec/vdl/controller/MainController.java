package com.github.engatec.vdl.controller;

import java.nio.file.Files;
import java.util.List;

import com.github.engatec.vdl.controller.components.DownloadableItemComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.UpdateManager;
import com.github.engatec.vdl.core.command.DownloadCommand;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.handler.CopyUrlFromClipboardOnFocusChangeListener;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.downloadable.CustomFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.model.preferences.wrapper.general.SkipDownloadableDetailsSearchPref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.component.DownloadableItemComponent;
import com.github.engatec.vdl.ui.component.SidebarComponent;
import com.github.engatec.vdl.ui.stage.AboutStage;
import com.github.engatec.vdl.ui.stage.HistoryStage;
import com.github.engatec.vdl.ui.stage.PreferencesStage;
import com.github.engatec.vdl.ui.stage.QueueStage;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.worker.service.DownloadableSearchService;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController extends StageAwareController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    private final ApplicationContext appCtx = ApplicationContext.INSTANCE;
    private final DownloadableSearchService downloadableSearchService = new DownloadableSearchService();

    @FXML private HBox mainHbox;

    @FXML private VBox rootControlVBox;

    @FXML private ScrollPane contentScrollPane;
    @FXML private VBox contentVBox;

    @FXML private Menu fileMenu;
    @FXML private MenuItem queueMenuItem;
    @FXML private MenuItem historyMenuItem;
    @FXML private MenuItem preferencesMenuItem;
    @FXML private MenuItem exitMenuItem;

    @FXML private Menu languageMenu;
    @FXML private MenuItem langEnMenuItem;
    @FXML private MenuItem langRuMenuItem;
    @FXML private MenuItem langUkMenuItem;

    @FXML private Menu helpMenu;
    @FXML private MenuItem checkYoutubeDlUpdatesMenuItem;
    @FXML private MenuItem aboutMenuItem;

    @FXML private TextField videoUrlTextField;
    @FXML private Button searchBtn;
    @FXML private ProgressIndicator searchProgressIndicator;

    @FXML private HBox playlistSearchProgressWrapperHBox;
    @FXML private ProgressBar playlistSearchProgressBar;
    @FXML private Label playlistSearchLabel;
    @FXML private Button playlistSearchCancelBtn;

    private MainController() {
    }

    public MainController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        mainHbox.getChildren().add(new SidebarComponent(stage).load());

        initLocaleBindings();
        initSearchBindings();
        initMenuItems();
        initDragAndDrop();
        initScrollPaneUpdate();

        stage.focusedProperty().addListener(new CopyUrlFromClipboardOnFocusChangeListener(videoUrlTextField, searchBtn));
    }

    private void initLocaleBindings() {
        I18n.bindLocaleProperty(fileMenu.textProperty(), "menu.file");
        I18n.bindLocaleProperty(queueMenuItem.textProperty(), "menu.file.queue");
        I18n.bindLocaleProperty(historyMenuItem.textProperty(), "menu.file.history");
        I18n.bindLocaleProperty(preferencesMenuItem.textProperty(), "menu.file.preferences");
        I18n.bindLocaleProperty(exitMenuItem.textProperty(), "menu.file.exit");
        I18n.bindLocaleProperty(languageMenu.textProperty(), "menu.language");
        I18n.bindLocaleProperty(helpMenu.textProperty(), "menu.help");
        I18n.bindLocaleProperty(checkYoutubeDlUpdatesMenuItem.textProperty(), "menu.help.update.youtubedl");
        I18n.bindLocaleProperty(aboutMenuItem.textProperty(), "menu.help.about");
        I18n.bindLocaleProperty(searchBtn.textProperty(), "search");
        I18n.bindLocaleProperty(playlistSearchCancelBtn.textProperty(), "button.cancel");
    }

    private void initSearchBindings() {
        searchBtn.managedProperty().bind(searchProgressIndicator.visibleProperty().not());
        searchBtn.visibleProperty().bind(searchProgressIndicator.visibleProperty().not());
        searchProgressIndicator.prefHeightProperty().bind(searchBtn.heightProperty());
        searchProgressIndicator.prefWidthProperty().bind(searchBtn.widthProperty());
        searchProgressIndicator.managedProperty().bind(searchProgressIndicator.visibleProperty());
        searchProgressIndicator.setVisible(false);
        searchBtn.setOnAction(this::handleSearchBtnClick);
        videoUrlTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchBtnClick(event);
            }
        });

        playlistSearchProgressWrapperHBox.managedProperty().bind(searchProgressIndicator.visibleProperty());
        playlistSearchProgressWrapperHBox.visibleProperty().bind(searchProgressIndicator.visibleProperty());
        playlistSearchCancelBtn.setOnAction(this::handlePlaylistSearchCancelBtnClick);
    }

    private void initMenuItems() {
        exitMenuItem.setOnAction(this::handleExitMenuItemClick);
        preferencesMenuItem.setOnAction(this::handlePreferencesMenuItemClick);
        checkYoutubeDlUpdatesMenuItem.setOnAction(this::handleYoutubeDlUpdatesMenuItemClick);
        aboutMenuItem.setOnAction(this::handleAboutMenuItemClick);
        queueMenuItem.setOnAction(this::handleQueueMenuItemClick);
        historyMenuItem.setOnAction(this::handleHistoryMenuItemClick);

        langEnMenuItem.setOnAction(event -> handleLanguageChange(event, Language.ENGLISH));
        langRuMenuItem.setOnAction(event -> handleLanguageChange(event, Language.RUSSIAN));
        langUkMenuItem.setOnAction(event -> handleLanguageChange(event, Language.UKRAINIAN));
    }

    private void handleExitMenuItemClick(ActionEvent event) {
        event.consume();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    private void handlePreferencesMenuItemClick(ActionEvent event) {
        new PreferencesStage().modal(stage).showAndWait();
        event.consume();
    }

    private void handleLanguageChange(ActionEvent event, Language language) {
        appCtx.setLanguage(language);
        LanguagePref languagePref = ConfigRegistry.get(LanguagePref.class);
        languagePref.setValue(language.getLocaleLanguage());
        languagePref.save();
        event.consume();
    }

    private void handleYoutubeDlUpdatesMenuItemClick(ActionEvent event) {
        if (Files.isWritable(appCtx.getYoutubeDlPath())) {
            UpdateManager.updateYoutubeDl(stage);
        } else {
            Dialogs.error("update.youtubedl.nopermissions");
        }
        event.consume();
    }

    private void handleAboutMenuItemClick(ActionEvent event) {
        new AboutStage().modal(stage).showAndWait();
        event.consume();
    }

    private void handleQueueMenuItemClick(ActionEvent event) {
        new QueueStage().modal(stage).showAndWait();
        event.consume();
    }

    private void handleHistoryMenuItemClick(ActionEvent event) {
        new HistoryStage().modal(stage).showAndWait();
        event.consume();
    }

    private void handleSearchBtnClick(Event event) {
        contentVBox.getChildren().clear();

        boolean autodownloadEnabled = ConfigRegistry.get(AutoDownloadPref.class).getValue();
        boolean skipDownloadableDetailsSearch = ConfigRegistry.get(SkipDownloadableDetailsSearchPref.class).getValue();
        if (autodownloadEnabled && skipDownloadableDetailsSearch) {
            performAutoDownload();
        } else {
            searchDownloadables();
        }

        event.consume();
    }

    private void handlePlaylistSearchCancelBtnClick(ActionEvent event) {
        downloadableSearchService.cancel();
        event.consume();
    }

    private void performAutoDownload() {
        final String format = ConfigRegistry.get(AutoDownloadFormatPref.class).getValue();
        Downloadable downloadable = new CustomFormatDownloadable(videoUrlTextField.getText(), format);
        AppUtils.executeCommandResolvingPath(stage, new DownloadCommand(stage, downloadable), downloadable::setDownloadPath);
    }

    private void searchDownloadables() {
        downloadableSearchService.setUrl(videoUrlTextField.getText());
        downloadableSearchService.setOnDownloadableReadyCallback(this::updateContentPane);

        downloadableSearchService.setOnSucceeded(it -> {
            List<MultiFormatDownloadable> downloadables = (List<MultiFormatDownloadable>) it.getSource().getValue();

            boolean autodownloadEnabled = ConfigRegistry.get(AutoDownloadPref.class).getValue();
            if (downloadables.size() > 1 && autodownloadEnabled) {
                setDownloadablesMassContextMenu(downloadables);
            }
            if (autodownloadEnabled && contentVBox.getChildren().size() == 1) {
                Platform.runLater(this::performAutoDownload); // runLater is to release the service and trigger runningProperty to be false
            }
        });

        downloadableSearchService.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
            Dialogs.info("video.search.error");
        });

        searchProgressIndicator.visibleProperty().bind(downloadableSearchService.runningProperty());
        playlistSearchProgressBar.progressProperty().bind(downloadableSearchService.progressProperty());
        playlistSearchLabel.textProperty().bind(downloadableSearchService.messageProperty());

        downloadableSearchService.restart();
    }

    private void updateContentPane(List<MultiFormatDownloadable> downloadables, Integer totalItems) {
        for (MultiFormatDownloadable downloadable : downloadables) {
            DownloadableItemComponentController node = new DownloadableItemComponent(stage, downloadable).load();
            node.setExpanded(totalItems == 1);
            contentVBox.getChildren().add(node);
        }
    }

    private void setDownloadablesMassContextMenu(List<MultiFormatDownloadable> downloadables) {
        for (Node node : contentVBox.getChildren()) {
            MenuItem addToQueueAllMenuItem = new MenuItem(appCtx.getResourceBundle().getString("component.downloadgrid.contextmenu.queue.addall"));
            addToQueueAllMenuItem.setOnAction(e -> {
                AppUtils.resolveDownloadPath(stage).ifPresent(path -> {
                    String format = ConfigRegistry.get(AutoDownloadFormatPref.class).getValue();
                    for (MultiFormatDownloadable item : downloadables) {
                        CustomFormatDownloadable customFormatDownloadable = new CustomFormatDownloadable(item.getBaseUrl(), format);
                        customFormatDownloadable.setTitle(item.getTitle());
                        customFormatDownloadable.setPostprocessingSteps(item.getPostprocessingSteps());
                        customFormatDownloadable.setDownloadPath(path);
                        new EnqueueCommand(customFormatDownloadable).execute();
                    }
                });
                e.consume();
            });

            ((DownloadableItemComponentController) node).getContextMenu().getItems().add(addToQueueAllMenuItem);
        }
    }

    private void initDragAndDrop() {
        rootControlVBox.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        });

        rootControlVBox.setOnDragDropped(e -> {
            Dragboard dragboard = e.getDragboard();
            if (e.getTransferMode() == TransferMode.COPY && dragboard.hasString()) {
                videoUrlTextField.setText(dragboard.getString());
                searchBtn.fire();
                e.setDropCompleted(true);
            }
            e.consume();
        });
    }

    /**
     * A hack!
     * This is to forcibly redraw scrollpane if no scrollbar is visible,
     * otherwise dynamically added playlist items not visible until the search is over or srollbar appears (whichever happens first).
     */
    private void initScrollPaneUpdate() {
        var snapshotParams = new SnapshotParameters();
        var writableImage = new WritableImage(1, 1);
        contentVBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            double scrollPaneHeight = contentScrollPane.getHeight();
            double contentVBoxHeight = contentVBox.getHeight();
            if (Double.compare(contentVBoxHeight, scrollPaneHeight) < 0) {
                contentScrollPane.snapshot(snapshotParams, writableImage);
            }
        });
    }
}
