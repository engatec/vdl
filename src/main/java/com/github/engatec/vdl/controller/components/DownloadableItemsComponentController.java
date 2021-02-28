package com.github.engatec.vdl.controller.components;

import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;

import com.github.engatec.vdl.controller.preferences.PostprocessingController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.UiComponent;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.downloadable.CustomFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadFormatConfigItem;
import com.github.engatec.vdl.ui.Stages;
import com.github.engatec.vdl.util.AppUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DownloadableItemsComponentController {

    private Stage stage;
    private List<MultiFormatDownloadable> downloadables;
    private Function<MultiFormatDownloadable, ? extends Parent> contentFunction;

    @FXML private VBox rootVBox;

    private DownloadableItemsComponentController() {
    }

    public DownloadableItemsComponentController(Stage stage, List<MultiFormatDownloadable> downloadables, Function<MultiFormatDownloadable, ? extends Parent> contentFunction) {
        this.stage = stage;
        this.downloadables = downloadables;
        this.contentFunction = contentFunction;
    }

    @FXML
    public void initialize() {
        rootVBox.setSpacing(4);

        boolean autodownloadEnabled = ConfigManager.INSTANCE.getValue(new AutoDownloadConfigItem());
        boolean singleItem = downloadables.size() == 1;
        for (MultiFormatDownloadable item : downloadables) {
            TitledPane tp = new TitledPane(item.getTitle(), contentFunction.apply(item));
            tp.setExpanded(singleItem);
            tp.setCollapsible(!singleItem);
            tp.getStyleClass().add("no-border");
            setContextMenu(tp, item, singleItem, autodownloadEnabled);
            rootVBox.getChildren().add(tp);
        }
    }

    private void setContextMenu(TitledPane tp, MultiFormatDownloadable downloadable, boolean singleItem, boolean autodownloadEnabled) {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        ContextMenu ctxMenu = new ContextMenu();

        MenuItem postprocessingMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.postprocessing"));
        postprocessingMenuItem.setOnAction(e -> {
            Stages.newModalStage(UiComponent.POSTPROCESSING, it -> new PostprocessingController(it, downloadable), stage).showAndWait();
            e.consume();
        });
        ctxMenu.getItems().add(postprocessingMenuItem);

        if (!singleItem && autodownloadEnabled) {
            MenuItem addToQueueMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.queue.add"));
            addToQueueMenuItem.setOnAction(e -> {
                String format = ConfigManager.INSTANCE.getValue(new AutoDownloadFormatConfigItem());
                CustomFormatDownloadable customFormatDownloadable = new CustomFormatDownloadable(downloadable.getBaseUrl(), format);
                customFormatDownloadable.setPostprocessingSteps(downloadable.getPostprocessingSteps());
                AppUtils.executeCommandResolvingPath(stage, new EnqueueCommand(customFormatDownloadable), customFormatDownloadable::setDownloadPath);
                e.consume();
            });
            ctxMenu.getItems().add(addToQueueMenuItem);
        }

        tp.setContextMenu(ctxMenu);
    }
}
