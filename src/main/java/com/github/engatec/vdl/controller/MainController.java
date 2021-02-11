package com.github.engatec.vdl.controller;

import java.io.UncheckedIOException;
import java.util.List;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.ConfigManager;
import com.github.engatec.vdl.core.ConfigProperty;
import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.UiManager;
import com.github.engatec.vdl.model.Audio;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.Video;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.worker.FetchDownloadableDataTask;
import com.github.engatec.vdl.worker.UpdateBinariesTask;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.collections4.CollectionUtils;

public class MainController implements StageAware {

    private ApplicationContext appCtx;
    private Stage stage;

    @FXML private VBox rootControlVBox;

    @FXML private TabPane videoAudioTabPane;
    @FXML private Tab videoTab;
    @FXML private ScrollPane videoTabScrollPane;
    @FXML private Tab audioTab;
    @FXML private ScrollPane audioTabScrollPane;

    @FXML private Menu fileMenu;
    @FXML private MenuItem checkUpdatesMenuItem;
    @FXML private MenuItem preferencesMenuItem;
    @FXML private MenuItem exitMenuItem;

    @FXML private Menu languageMenu;
    @FXML private MenuItem langEnMenuItem;
    @FXML private MenuItem langRuMenuItem;

    @FXML private TextField videoUrlTextField;
    @FXML private Button searchBtn;
    @FXML private ProgressIndicator searchProgressIndicator;

    @FXML
    public void initialize() {
        appCtx = ApplicationContext.INSTANCE;

        setLocaleBindings();
        initSearchHandlers();
        setTabBindings();

        checkUpdatesMenuItem.setOnAction(e -> Dialogs.progress(appCtx.getResourceBundle().getString("dialog.progress.title.label.updateinprogress"), stage, new UpdateBinariesTask()));
        preferencesMenuItem.setOnAction(this::handlePreferencesClick);
        exitMenuItem.setOnAction(this::handleExitClick);

        langEnMenuItem.setOnAction(event -> handleLanguageChange(event, Language.ENGLISH));
        langRuMenuItem.setOnAction(event -> handleLanguageChange(event, Language.RUSSIAN));

        setDragAndDrop();
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void setLocaleBindings() {
        I18n.bindLocaleProperty(fileMenu.textProperty(), "menu.file");
        I18n.bindLocaleProperty(checkUpdatesMenuItem.textProperty(), "menu.file.checkupdates");
        I18n.bindLocaleProperty(preferencesMenuItem.textProperty(), "menu.file.preferences");
        I18n.bindLocaleProperty(exitMenuItem.textProperty(), "menu.file.exit");
        I18n.bindLocaleProperty(languageMenu.textProperty(), "menu.language");
        I18n.bindLocaleProperty(searchBtn.textProperty(), "search");
        I18n.bindLocaleProperty(videoTab.textProperty(), "video");
        I18n.bindLocaleProperty(audioTab.textProperty(), "audio");
    }

    private void initSearchHandlers() {
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

    private void setTabBindings() {
        BooleanBinding hasVideoBinding = videoTabScrollPane.contentProperty().isNotNull();
        BooleanBinding hasAudioBinding = audioTabScrollPane.contentProperty().isNotNull();
        BooleanBinding hasVideoOrAudioBinding = Bindings.or(hasVideoBinding, hasAudioBinding);
        videoTab.disableProperty().bind(hasVideoBinding.not());
        audioTab.disableProperty().bind(hasAudioBinding.not());
        videoAudioTabPane.visibleProperty().bind(hasVideoOrAudioBinding);
    }

    private void handleExitClick(ActionEvent event) {
        event.consume();
        Platform.exit();
    }

    private void handlePreferencesClick(ActionEvent event) {
        Stage prefStage = UiManager.loadStage(UiComponent.PREFERENCES);
        prefStage.setResizable(false);
        prefStage.initModality(Modality.APPLICATION_MODAL);
        prefStage.initOwner(this.stage);
        prefStage.setTitle(appCtx.getResourceBundle().getString("preferences.title"));
        prefStage.showAndWait();
        event.consume();
    }

    private void handleLanguageChange(ActionEvent event, Language language) {
        appCtx.setLanguage(language);
        ConfigManager.INSTANCE.setValue(ConfigProperty.LANGUAGE, language.getLocaleLanguage());
        try {
            ConfigManager.INSTANCE.saveConfig();
        } catch (UncheckedIOException ignored) {
            // Не смогли язык сохранить :( Что ж, в следующий раз загрузится с дефолтным :)
        }
        event.consume();
    }

    private void handleSearchEvent(Event event) {
        videoTabScrollPane.setContent(null);
        audioTabScrollPane.setContent(null);

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
        task.setOnFailed(it -> Dialogs.info(appCtx.getResourceBundle().getString("video.search.error")));
        searchProgressIndicator.visibleProperty().bind(task.runningProperty());
        appCtx.runTaskAsync(task);
        event.consume();
    }

    private void setDragAndDrop() {
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
                e.setDropCompleted(true);
                searchBtn.fire();
            }
            e.consume();
        });
    }
}
