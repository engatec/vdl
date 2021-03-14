package com.github.engatec.vdl.component;

import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.components.DownloadableItemComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.downloadable.CustomFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadFormatConfigItem;
import com.github.engatec.vdl.stage.postprocessing.PostprocessingStage;
import com.github.engatec.vdl.util.AppUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class DownloadableItemComponent extends AppComponent<DownloadableItemComponentController> {

    private final MultiFormatDownloadable downloadable;

    public DownloadableItemComponent(Stage stage, MultiFormatDownloadable downloadable) {
        super(stage);
        this.downloadable = downloadable;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/downloadable-item-component.fxml";
    }

    @Override
    protected DownloadableItemComponentController getController() {
        return new DownloadableItemComponentController(stage, downloadable);
    }

    @Override
    public DownloadableItemComponentController load() {
        DownloadableItemComponentController item = super.load();
        item.setText(downloadable.getTitle());
        item.getStyleClass().add("no-border");
        item.setContextMenu(createContextMenu());
        return item;
    }

    private ContextMenu createContextMenu() {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        ContextMenu ctxMenu = new ContextMenu();

        MenuItem postprocessingMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.postprocessing"));
        postprocessingMenuItem.setOnAction(e -> {
            new PostprocessingStage(downloadable).modal(stage).showAndWait();
            e.consume();
        });
        ctxMenu.getItems().add(postprocessingMenuItem);

        boolean autodownloadEnabled = ConfigManager.INSTANCE.getValue(new AutoDownloadConfigItem());
        if (autodownloadEnabled) {
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

        return ctxMenu;
    }
}
