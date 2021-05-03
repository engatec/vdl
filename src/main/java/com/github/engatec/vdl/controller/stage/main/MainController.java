package com.github.engatec.vdl.controller.stage.main;

import java.nio.file.Files;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.controller.component.SearchComponentController;
import com.github.engatec.vdl.controller.component.sidebar.SidebarComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.UpdateManager;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.Language;
import com.github.engatec.vdl.model.preferences.wrapper.general.LanguagePref;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.ui.component.SearchComponent;
import com.github.engatec.vdl.ui.component.SidebarComponent;
import com.github.engatec.vdl.ui.stage.AboutStage;
import com.github.engatec.vdl.ui.stage.HistoryStage;
import com.github.engatec.vdl.ui.stage.PreferencesStage;
import com.github.engatec.vdl.ui.stage.QueueStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainController extends StageAwareController {

    private static final Logger LOGGER = LogManager.getLogger(MainController.class);

    private final ApplicationContext appCtx = ApplicationContext.INSTANCE;

    @FXML private HBox rootNode;
    @FXML private StackPane navigationPane;
    @FXML private StackPane contentPane;

    private SidebarComponentController sideBar;
    private SearchComponentController search;

    /*@FXML private HBox mainHbox;

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
    @FXML private Button playlistSearchCancelBtn;*/

    private MainController() {
    }

    public MainController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        sideBar = new SidebarComponent(stage).load();
        navigationPane.getChildren().add(sideBar);

        search = new SearchComponent(stage).load();
        contentPane.getChildren().add(search);


        initLocaleBindings();
        initSearchBindings();
        initMenuItems();
        initDragAndDrop();
        initScrollPaneUpdate();

        // stage.focusedProperty().addListener(new CopyUrlFromClipboardOnFocusChangeListener(videoUrlTextField, searchBtn));
    }

    private void setAnchors() {

    }

    private void initLocaleBindings() {
        /*I18n.bindLocaleProperty(fileMenu.textProperty(), "menu.file");
        I18n.bindLocaleProperty(queueMenuItem.textProperty(), "menu.file.queue");
        I18n.bindLocaleProperty(historyMenuItem.textProperty(), "menu.file.history");
        I18n.bindLocaleProperty(preferencesMenuItem.textProperty(), "menu.file.preferences");
        I18n.bindLocaleProperty(exitMenuItem.textProperty(), "menu.file.exit");
        I18n.bindLocaleProperty(languageMenu.textProperty(), "menu.language");
        I18n.bindLocaleProperty(helpMenu.textProperty(), "menu.help");
        I18n.bindLocaleProperty(checkYoutubeDlUpdatesMenuItem.textProperty(), "menu.help.update.youtubedl");
        I18n.bindLocaleProperty(aboutMenuItem.textProperty(), "menu.help.about");
        I18n.bindLocaleProperty(searchBtn.textProperty(), "search");
        I18n.bindLocaleProperty(playlistSearchCancelBtn.textProperty(), "button.cancel");*/
    }

    private void initSearchBindings() {
        /*searchBtn.managedProperty().bind(searchProgressIndicator.visibleProperty().not());
        searchBtn.visibleProperty().bind(searchProgressIndicator.visibleProperty().not());
        searchProgressIndicator.prefHeightProperty().bind(searchBtn.heightProperty());
        searchProgressIndicator.prefWidthProperty().bind(searchBtn.widthProperty());
        searchProgressIndicator.managedProperty().bind(searchProgressIndicator.visibleProperty());
        searchProgressIndicator.setVisible(false);
        searchBtn.setOnAction(this::handleSearchBtnClick);
        playlistSearchProgressWrapperHBox.managedProperty().bind(searchProgressIndicator.visibleProperty());
        playlistSearchProgressWrapperHBox.visibleProperty().bind(searchProgressIndicator.visibleProperty());
        playlistSearchCancelBtn.setOnAction(this::handlePlaylistSearchCancelBtnClick);*/
    }

    private void initMenuItems() {
        /*exitMenuItem.setOnAction(this::handleExitMenuItemClick);
        preferencesMenuItem.setOnAction(this::handlePreferencesMenuItemClick);
        checkYoutubeDlUpdatesMenuItem.setOnAction(this::handleYoutubeDlUpdatesMenuItemClick);
        aboutMenuItem.setOnAction(this::handleAboutMenuItemClick);
        queueMenuItem.setOnAction(this::handleQueueMenuItemClick);
        historyMenuItem.setOnAction(this::handleHistoryMenuItemClick);

        langEnMenuItem.setOnAction(event -> handleLanguageChange(event, Language.ENGLISH));
        langRuMenuItem.setOnAction(event -> handleLanguageChange(event, Language.RUSSIAN));
        langUkMenuItem.setOnAction(event -> handleLanguageChange(event, Language.UKRAINIAN));*/
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


    private void performAutoDownload() {
        /*final String format = ConfigRegistry.get(AutoDownloadFormatPref.class).getValue();
        Downloadable downloadable = new BaseDownloadable(videoUrlTextField.getText(), format);
        AppUtils.executeCommandResolvingPath(stage, new DownloadCommand(stage, downloadable), downloadable::setDownloadPath);*/
    }


    private void initDragAndDrop() {
        /*rootControlVBox.setOnDragOver(e -> {
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
        });*/
    }

    /**
     * A hack!
     * This is to forcibly redraw scrollpane if no scrollbar is visible,
     * otherwise dynamically added playlist items not visible until the search is over or srollbar appears (whichever happens first).
     */
    private void initScrollPaneUpdate() {
        /*var snapshotParams = new SnapshotParameters();
        var writableImage = new WritableImage(1, 1);
        contentVBox.getChildren().addListener((ListChangeListener<Node>) c -> {
            double scrollPaneHeight = contentScrollPane.getHeight();
            double contentVBoxHeight = contentVBox.getHeight();
            if (Double.compare(contentVBoxHeight, scrollPaneHeight) < 0) {
                contentScrollPane.snapshot(snapshotParams, writableImage);
            }
        });*/
    }
}
