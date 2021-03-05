package com.github.engatec.vdl.controller.postprocessing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.engatec.vdl.controller.StageAwareController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.model.postprocessing.Postprocessing;
import com.github.engatec.vdl.stage.postprocessing.PostprocessingStageFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class PostprocessingController extends StageAwareController {

    private Downloadable downloadable;

    private final Map<Class<? extends Postprocessing>, Postprocessing> appliedPostprocessingsMap = new HashMap<>();

    @FXML private ListView<PostprocessingStageFactory> availableItemsListView;
    @FXML private ListView<PostprocessingStageFactory> activeItemsListView;

    @FXML private Button okButton;
    @FXML private Button cancelButton;

    private PostprocessingController() {
    }

    public PostprocessingController(Stage stage, Downloadable downloadable) {
        super(stage);
        this.downloadable = downloadable;
        for (Postprocessing item : downloadable.getPostprocessingSteps()) {
            appliedPostprocessingsMap.put(item.getClass(), item);
        }
    }

    @FXML
    public void initialize() throws ParseException {
        PostprocessingStageFactory[] postProcessingFactories = PostprocessingStageFactory.values();
        availableItemsListView.setItems(FXCollections.observableArrayList(postProcessingFactories));
        for (PostprocessingStageFactory item : postProcessingFactories) {
            if (appliedPostprocessingsMap.containsKey(item.getPostprocessingClass())) {
                activeItemsListView.getItems().add(item);
            }
        }

        availableItemsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PostprocessingStageFactory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setOnMouseClicked(null);
                    setContextMenu(null);
                } else {
                    setText(item.toString());
                    setOnMouseClicked(createMouseClickHandler(item));
                }
            }
        });

        activeItemsListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(PostprocessingStageFactory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setOnMouseClicked(null);
                    setContextMenu(null);
                } else {
                    setText(item.toString());
                    setOnMouseClicked(createMouseClickHandler(item));
                    setContextMenu(createContextMenu(item));
                }
            }
        });

        okButton.setOnAction(this::handleOkButtonClick);
        cancelButton.setOnAction(this::handleCancelButtonClick);
    }

    private EventHandler<? super MouseEvent> createMouseClickHandler(PostprocessingStageFactory factory) {
        return (EventHandler<MouseEvent>) event -> {
            if (factory == null) {
                event.consume();
                return;
            }

            MouseButton clickedButton = event.getButton();

            if (clickedButton == MouseButton.PRIMARY) {
                if (event.getClickCount() != 2) {
                    return;
                }

                factory.create(
                        appliedPostprocessingsMap.get(factory.getPostprocessingClass()),
                        param -> {
                            appliedPostprocessingsMap.put(factory.getPostprocessingClass(), param);
                            ObservableList<PostprocessingStageFactory> activeItems = activeItemsListView.getItems();
                            if (!activeItems.contains(factory)) {
                                activeItems.add(factory);
                            }
                        }
                ).modal(stage).showAndWait();
            }

            event.consume();
        };
    }

    private ContextMenu createContextMenu(PostprocessingStageFactory factory) {
        ContextMenu ctxMenu = new ContextMenu();
        MenuItem removeMenuItem = new MenuItem(ApplicationContext.INSTANCE.getResourceBundle().getString("stage.postprocessing.contextmenu.remove"));
        removeMenuItem.setOnAction(e -> {
            appliedPostprocessingsMap.remove(factory.getPostprocessingClass());
            activeItemsListView.getItems().remove(factory);
            e.consume();
        });

        ctxMenu.getItems().addAll(removeMenuItem);
        return ctxMenu;
    }

    private void handleOkButtonClick(ActionEvent event) {
        List<Postprocessing> postprocessings = new ArrayList<>(appliedPostprocessingsMap.values());
        downloadable.setPostprocessingSteps(postprocessings);
        stage.close();
        event.consume();
    }

    private void handleCancelButtonClick(ActionEvent event) {
        stage.close();
        event.consume();
    }
}
