package com.github.engatec.vdl.controller.preferences;

import java.util.List;
import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.StageAware;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.category.Category;
import com.github.engatec.vdl.core.preferences.category.GeneralCategory;
import com.github.engatec.vdl.core.preferences.category.YoutubeDlCategory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class PreferencesController implements StageAware {

    private Stage stage;

    private final ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

    @FXML private ScrollPane preferencesScrollPane;
    @FXML private TreeView<Category> preferencesCategoryTreeView;

    @FXML private Button okBtn;
    @FXML private Button cancelBtn;

    public PreferencesController() {
    }

    @FXML
    public void initialize() {
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
        selectionModel.select(0);
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle(resourceBundle.getString("preferences.title"));
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
        stage.close();
        event.consume();
    }

    private void handleOkBtnClick(ActionEvent event) {
        saveSettings();
        stage.close();
        event.consume();
    }

    private void saveSettings() {
        for (TreeItem<Category> child : preferencesCategoryTreeView.getRoot().getChildren()) {
            child.getValue().savePreferences();
        }
    }
}
