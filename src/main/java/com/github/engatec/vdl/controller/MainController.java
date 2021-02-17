package com.github.engatec.vdl.controller;

import java.nio.file.Files;
import java.util.List;

import com.github.engatec.vdl.controller.preferences.PreferencesController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.core.UpdateManager;
import com.github.engatec.vdl.core.action.DownloadAction;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.core.preferences.handler.CopyUrlFromClipboardOnFocusChangeListener;
import com.github.engatec.vdl.model.Audio;
import com.github.engatec.vdl.model.Downloadable;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.Video;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadCustomFormatConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadUseCustomFormatConfigItem;
import com.github.engatec.vdl.model.preferences.general.LanguageConfigItem;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.Stages;
import com.github.engatec.vdl.worker.FetchDownloadableDataTask;
import com.github.engatec.vdl.worker.data.DownloadableData;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.concurrent.Task;
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

    private ApplicationContext appCtx;

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
        appCtx = ApplicationContext.INSTANCE;

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
        ConfigManager cfg = ConfigManager.INSTANCE;

        boolean useCustomFormat = cfg.getValue(new AutoDownloadUseCustomFormatConfigItem());
        final String format = useCustomFormat ? cfg.getValue(new AutoDownloadCustomFormatConfigItem()) : "bestvideo+bestaudio/best";
        Downloadable downloadable = new Downloadable() {
            @Override
            public String getBaseUrl() {
                return videoUrlTextField.getText();
            }

            @Override
            public String getFormatId() {
                return format;
            }
        };
        new DownloadAction(stage, downloadable).perform();
    }

    private void searchDownloadables() {
        Task<DownloadableData> task = new FetchDownloadableDataTask(videoUrlTextField.getText());
        task.setOnSucceeded(it -> {
            DownloadableData downloadableData = (DownloadableData) it.getSource().getValue();
            List<Video> videoList = downloadableData.getVideoList();
            List<Audio> audioList = downloadableData.getAudioList();

            if (CollectionUtils.isNotEmpty(videoList)) {
                Parent videoComponent = UiManager.loadComponent(UiComponent.VIDEO_DOWNLOAD_GRID, param -> new VideoDownloadGridController(stage, videoList, audioList));
                videoTabScrollPane.setContent(videoComponent);
            }

            if (CollectionUtils.isNotEmpty(audioList)) {
                Parent audioComponent = UiManager.loadComponent(UiComponent.AUTIO_DOWNLOAD_GRID, param -> new AudioDownloadGridController(stage, audioList));
                audioTabScrollPane.setContent(audioComponent);
            }
        });
        task.setOnFailed(it -> {
            Throwable ex = it.getSource().getException();
            LOGGER.error(ex.getMessage(), ex);
            Dialogs.info(appCtx.getResourceBundle().getString("video.search.error"));
        });
        searchProgressIndicator.visibleProperty().bind(task.runningProperty());
        appCtx.runTaskAsync(task);
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
