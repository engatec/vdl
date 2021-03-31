package com.github.engatec.vdl.ui.component;

import java.util.List;
import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.components.DownloadableItemComponentController;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.command.DownloadCommand;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.core.preferences.ConfigRegistry;
import com.github.engatec.vdl.model.AudioFormat;
import com.github.engatec.vdl.model.downloadable.CustomFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.postprocessing.ExtractAudioPostprocessing;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadFormatPref;
import com.github.engatec.vdl.model.preferences.wrapper.general.AutoDownloadPref;
import com.github.engatec.vdl.ui.stage.postprocessing.PostprocessingStage;
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

        MenuItem postprocessingMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.contextmenu.postprocessing"));
        postprocessingMenuItem.setOnAction(e -> {
            new PostprocessingStage(downloadable).modal(stage).showAndWait();
            e.consume();
        });
        ctxMenu.getItems().add(postprocessingMenuItem);

        MenuItem mp3DownloadMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.contextmenu.mp3download"));
        mp3DownloadMenuItem.setOnAction(e -> {
            CustomFormatDownloadable mp3Downloadable = new CustomFormatDownloadable(downloadable.getBaseUrl(), downloadable.getFormatId());
            mp3Downloadable.setPostprocessingSteps(List.of(ExtractAudioPostprocessing.newInstance(AudioFormat.MP3.toString(), 0)));
            AppUtils.executeCommandResolvingPath(stage, new DownloadCommand(stage, mp3Downloadable), mp3Downloadable::setDownloadPath);
            e.consume();
        });
        ctxMenu.getItems().add(mp3DownloadMenuItem);

        boolean autodownloadEnabled = ConfigRegistry.get(AutoDownloadPref.class).getValue();
        if (autodownloadEnabled) {
            MenuItem addToQueueMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.contextmenu.queue.add"));
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
