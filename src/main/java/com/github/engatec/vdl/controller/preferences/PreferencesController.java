package com.github.engatec.vdl.controller.preferences;

import java.util.List;
import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.preferences.category.Category;
import com.github.engatec.vdl.core.preferences.category.GeneralCategory;
import com.github.engatec.vdl.core.preferences.category.YoutubeDlCategory;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadPref;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

public class PreferencesController extends StageAwareController {

    private final ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

    @FXML private ScrollPane preferencesScrollPane;
    @FXML private TreeView<Category> preferencesCategoryTreeView;

    @FXML private Button okBtn;
    @FXML private Button cancelBtn;

    private PreferencesController() {
    }

    public PreferencesController(Stage stage) {
        super(stage);
    }

    @FXML
    public void initialize() {
        stage.setTitle(resourceBundle.getString("preferences.title"));
        stage.setOnCloseRequest(event -> discardChanges());

        okBtn.setOnAction(this::handleOkBtnClick);
        cancelBtn.setOnAction(this::handleCancelBtnClick);

        List<TreeItem<Category>> categories = List.of(
                createGeneral(),
                createYoutubeDl()
        );
        TreeItem<Category> root = createRoot();
        root.getChildren().addAll(categories);

        MultipleSelectionModel<TreeItem<Category>> selectionModel = preferencesCategoryTreeView.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Node categoryUi = newValue.getValue().buildCategoryUi(stage);
            preferencesScrollPane.setContent(categoryUi);
        });
        selectionModel.selectFirst();
    }

    private TreeItem<Category> createRoot() {
        var root = new TreeItem<Category>();
        preferencesCategoryTreeView.setRoot(root);
        preferencesCategoryTreeView.setShowRoot(false);
        return root;
    }

    private TreeItem<Category> createGeneral() {
        return new TreeItem<>(new GeneralCategory(resourceBundle.getString("preferences.category.general")));
    }

    private TreeItem<Category> createYoutubeDl() {
        return new TreeItem<>(new YoutubeDlCategory(resourceBundle.getString("preferences.category.youtubedl")));
    }

    private void handleCancelBtnClick(ActionEvent event) {
        discardChanges();
        stage.close();
        event.consume();
    }

    private void discardChanges() {
        ConfigRegistry.restorePreviousValues();
    }

    private void handleOkBtnClick(ActionEvent event) {
        fixState();
        saveSettings();
        stage.close();
        event.consume();
    }

    private void fixState() {
        AutoDownloadFormatPref autoDownloadFormat = ConfigRegistry.get(AutoDownloadFormatPref.class);
        if (StringUtils.isBlank(autoDownloadFormat.getValue())) {
            autoDownloadFormat.setValue(StringUtils.EMPTY); // Reset the field if user entered multiple space characters
        }

        AutoDownloadPref autoDownload = ConfigRegistry.get(AutoDownloadPref.class);
        if (autoDownload.getValue() && StringUtils.isBlank(autoDownloadFormat.getValue())) {
            autoDownload.setValue(false);
        }
    }

    private void saveSettings() {
        ConfigRegistry.saveAll();
    }
}
