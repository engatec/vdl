package com.github.engatec.vdl.component;

import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.components.DownloadableItemComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.downloadable.CustomFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadPref;
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

        boolean autodownloadEnabled = ConfigRegistry.get(AutoDownloadPref.class).getValue();
        if (autodownloadEnabled) {
            MenuItem addToQueueMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.queue.add"));
            addToQueueMenuItem.setOnAction(e -> {
                String format = ConfigRegistry.get(AutoDownloadFormatPref.class).getValue();
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
