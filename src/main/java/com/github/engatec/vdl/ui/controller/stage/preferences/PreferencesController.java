package com.github.engatec.vdl.ui.controller.stage.preferences;

import java.util.List;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.core.preferences.category.Category;
import com.github.engatec.vdl.core.preferences.category.GeneralCategory;
import com.github.engatec.vdl.core.preferences.category.YoutubeDlCategory;
import com.github.engatec.vdl.ui.controller.stage.StageAwareController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class PreferencesController extends StageAwareController {

    private final ApplicationContext ctx = ApplicationContext.getInstance();
    private final ConfigRegistry configRegistry = ctx.getConfigRegistry();

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
        stage.setTitle(ctx.getLocalizedString("preferences.title"));
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
        return new TreeItem<>(new GeneralCategory(ctx.getLocalizedString("preferences.category.general")));
    }

    private TreeItem<Category> createYoutubeDl() {
        return new TreeItem<>(new YoutubeDlCategory(ctx.getLocalizedString("preferences.category.youtubedl")));
    }

    private void handleCancelBtnClick(ActionEvent event) {
        discardChanges();
        stage.close();
        event.consume();
    }

    private void discardChanges() {
        configRegistry.dropUnsavedChanges();
    }

    private void handleOkBtnClick(ActionEvent event) {
        for (TreeItem<Category> item : preferencesCategoryTreeView.getRoot().getChildren()) {
            if (item.getValue().hasErrors()) {
                preferencesCategoryTreeView.getSelectionModel().select(item);
                event.consume();
                return;
            }
        }

        saveSettings();
        stage.close();
        event.consume();
    }

    private void saveSettings() {
        configRegistry.saveAll();
    }
}
