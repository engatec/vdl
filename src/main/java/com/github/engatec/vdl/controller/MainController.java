package com.github.engatec.vdl.controller;

import java.nio.file.Files;
import java.util.List;

import com.github.engatec.vdl.controller.components.DownloadableItemsComponentController;
import com.github.engatec.vdl.controller.preferences.PreferencesController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.UpdateManager;
import com.github.engatec.vdl.core.action.DownloadAction;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.handler.CopyUrlFromClipboardOnFocusChangeListener;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.downloadable.BasicDownloadable;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadFormatConfigItem;
import com.github.engatec.vdl.model.preferences.general.LanguageConfigItem;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.Stages;
import com.github.engatec.vdl.util.ActionUtils;
import com.github.engatec.vdl.worker.data.DownloadableData;
import com.github.engatec.vdl.worker.service.DownloadableSearchService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController extends StageAwareController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    private final ApplicationContext appCtx = ApplicationContext.INSTANCE;
    private final DownloadableSearchService downloadableSearchService = new DownloadableSearchService();

    @FXML private VBox rootControlVBox;

    @FXML private TabPane videoAudioTabPane;
    @FXML private Tab videoTab;
    @FXML private ScrollPane videoTabScrollPane;
    @FXML private Tab audioTab;
    @FXML private ScrollPane audioTabScrollPane;

    @FXML private Menu fileMenu;
    @FXML private MenuItem downloadQueueMenuItem;
    @FXML private MenuItem preferencesMenuItem;
    @FXML private MenuItem exitMenuItem;

    @FXML private Menu languageMenu;
    @FXML private MenuItem langEnMenuItem;
    @FXML private MenuItem langRuMenuItem;

    @FXML private Menu helpMenu;
    @FXML private MenuItem checkYoutubeDlUpdatesMenuItem;
    @FXML private MenuItem aboutMenuItem;

    @FXML private TextField videoUrlTextField;
    @FXML private Button searchBtn;
    @FXML private ProgressIndicator searchProgressIndicator;

    private MainController() {
    }

    public MainController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        initLocaleBindings();
        initSearchBindings();
        initTabBindings();
        initMenuItems();
        initDragAndDrop();

        stage.focusedProperty().addListener(new CopyUrlFromClipboardOnFocusChangeListener(videoUrlTextField, searchBtn));
    }

    private void initLocaleBindings() {
        I18n.bindLocaleProperty(fileMenu.textProperty(), "menu.file");
        I18n.bindLocaleProperty(downloadQueueMenuItem.textProperty(), "menu.file.queue");
        I18n.bindLocaleProperty(preferencesMenuItem.textProperty(), "menu.file.preferences");
        I18n.bindLocaleProperty(exitMenuItem.textProperty(), "menu.file.exit");
        I18n.bindLocaleProperty(languageMenu.textProperty(), "menu.language");
        I18n.bindLocaleProperty(helpMenu.textProperty(), "menu.help");
        I18n.bindLocaleProperty(checkYoutubeDlUpdatesMenuItem.textProperty(), "menu.help.update.youtubedl");
        I18n.bindLocaleProperty(aboutMenuItem.textProperty(), "menu.help.about");
        I18n.bindLocaleProperty(searchBtn.textProperty(), "search");
        I18n.bindLocaleProperty(videoTab.textProperty(), "video");
        I18n.bindLocaleProperty(audioTab.textProperty(), "audio");
    }

    private void initSearchBindings() {
        searchBtn.managedProperty().bind(searchProgressIndicator.visibleProperty().not());
        searchBtn.visibleProperty().bind(searchProgressIndicator.visibleProperty().not());
        searchProgressIndicator.prefHeightProperty().bind(searchBtn.heightProperty());
        searchProgressIndicator.prefWidthProperty().bind(searchBtn.widthProperty());
        searchProgressIndicator.managedProperty().bind(searchProgressIndicator.visibleProperty());
        searchProgressIndicator.setVisible(false);
        searchBtn.setOnAction(this::handleSearchEvent);
        videoUrlTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchEvent(event);
            }
        });
    }

    private void initTabBindings() {
        BooleanBinding hasVideoBinding = videoTabScrollPane.contentProperty().isNotNull();
        BooleanBinding hasAudioBinding = audioTabScrollPane.contentProperty().isNotNull();
        BooleanBinding hasVideoOrAudioBinding = Bindings.or(hasVideoBinding, hasAudioBinding);
        videoTab.disableProperty().bind(hasVideoBinding.not());
        audioTab.disableProperty().bind(hasAudioBinding.not());
        videoAudioTabPane.visibleProperty().bind(hasVideoOrAudioBinding);
    }

    private void initMenuItems() {
        exitMenuItem.setOnAction(this::handleExitMenuItemClick);
        preferencesMenuItem.setOnAction(this::handlePreferencesMenuItemClick);
        checkYoutubeDlUpdatesMenuItem.setOnAction(this::handleYoutubeDlUpdatesMenuItemClick);
        aboutMenuItem.setOnAction(this::handleAboutMenuItemClick);
        downloadQueueMenuItem.setOnAction(this::handleDownloadQueueMenuItemClick);

        langEnMenuItem.setOnAction(event -> handleLanguageChange(event, Language.ENGLISH));
        langRuMenuItem.setOnAction(event -> handleLanguageChange(event, Language.RUSSIAN));
    }

    private void handleExitMenuItemClick(ActionEvent event) {
        event.consume();
        Platform.exit();
    }

    private void handlePreferencesMenuItemClick(ActionEvent event) {
        Stage prefStage = Stages.newModalStage(UiComponent.PREFERENCES, PreferencesController::new, this.stage);
        // Убрать хардкод в проперти
        prefStage.setMinWidth(600);
        prefStage.setMinHeight(400);
        prefStage.showAndWait();
        event.consume();
    }

    private void handleLanguageChange(ActionEvent event, Language language) {
        appCtx.setLanguage(language);
        ConfigManager.INSTANCE.setValue(new LanguageConfigItem(), language.getLocaleLanguage());
        event.consume();
    }

    private void handleYoutubeDlUpdatesMenuItemClick(ActionEvent event) {
        if (Files.isWritable(appCtx.getYoutubeDlPath())) {
            UpdateManager.updateYoutubeDl(stage);
        } else {
            Dialogs.error(appCtx.getResourceBundle().getString("update.youtubedl.nopermissions"));
        }
        event.consume();
    }

    private void handleAboutMenuItemClick(ActionEvent event) {
        Stage s = Stages.newModalStage(UiComponent.ABOUT, AboutController::new, this.stage);
        s.setResizable(false);
        s.showAndWait();
        event.consume();
    }

    private void handleDownloadQueueMenuItemClick(ActionEvent event) {
        Stages.newModalStage(UiComponent.QUEUE, QueueController::new, this.stage).showAndWait();
        event.consume();
    }

    private void handleSearchEvent(Event event) {
        videoTabScrollPane.setContent(null);
        audioTabScrollPane.setContent(null);

        boolean autodownloadEnabled = ConfigManager.INSTANCE.getValue(new AutoDownloadConfigItem());
        if (autodownloadEnabled) {
            performAutoDownload();
        } else {
            searchDownloadables();
        }

        event.consume();
    }

    private void performAutoDownload() {
        final String format = ConfigManager.INSTANCE.getValue(new AutoDownloadFormatConfigItem());
        Downloadable downloadable = new BasicDownloadable(videoUrlTextField.getText(), format);
        ActionUtils.performActionResolvingPath(stage, new DownloadAction(stage, downloadable), downloadable::setDownloadPath);
    }

    private void searchDownloadables() {
        downloadableSearchService.setUrl(videoUrlTextField.getText());

        downloadableSearchService.setOnSucceeded(it -> {
            List<DownloadableData> downloadableDataList = (List<DownloadableData>) it.getSource().getValue();
            loadVideoTab(downloadableDataList);
            loadAudioTab(downloadableDataList);
        });

        downloadableSearchService.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
            Dialogs.info(appCtx.getResourceBundle().getString("video.search.error"));
        });

        searchProgressIndicator.visibleProperty().bind(downloadableSearchService.runningProperty());

        downloadableSearchService.restart();
    }

    private void loadVideoTab(List<DownloadableData> downloadableDataList) {
        boolean hasVideos = false;
        for (DownloadableData item : downloadableDataList) {
            if (CollectionUtils.isNotEmpty(item.getVideoList())) {
                hasVideos = true;
                break;
            }
        }

        if (hasVideos) {
            Parent videoComponent = UiManager.loadComponent(
                    UiComponent.DOWNLOADABLE_ITEMS_COMPONENT,
                    param -> new DownloadableItemsComponentController(
                            stage,
                            downloadableDataList,
                            (videos, audios) -> UiManager.loadComponent(UiComponent.VIDEO_DOWNLOAD_GRID, param1 -> new VideoDownloadGridController(stage, videos, audios))
                    )
            );
            videoTabScrollPane.setContent(videoComponent);
        }
    }

    private void loadAudioTab(List<DownloadableData> downloadableDataList) {
        boolean hasAudios = false;
        for (DownloadableData item : downloadableDataList) {
            if (CollectionUtils.isNotEmpty(item.getAudioList())) {
                hasAudios = true;
                break;
            }
        }

        if (hasAudios) {
            Parent audioComponent = UiManager.loadComponent(
                    UiComponent.DOWNLOADABLE_ITEMS_COMPONENT,
                    param -> new DownloadableItemsComponentController(
                            stage,
                            downloadableDataList,
                            (videos, audios) -> UiManager.loadComponent(UiComponent.AUTIO_DOWNLOAD_GRID, param1 -> new AudioDownloadGridController(stage, audios))
                    )
            );
            audioTabScrollPane.setContent(audioComponent);
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
}
