package com.github.engatec.vdl.ui.component;

import java.util.List;
import java.util.ResourceBundle;

import com.github.engatec.vdl.controller.component.DownloadableItemComponentControllerLegacy;
import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.command.DownloadCommand;
import com.github.engatec.vdl.model.AudioFormat;
import com.github.engatec.vdl.model.downloadable.BaseDownloadable;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import com.github.engatec.vdl.model.postprocessing.ExtractAudioPostprocessing;
import com.github.engatec.vdl.ui.stage.postprocessing.PostprocessingStage;
import com.github.engatec.vdl.util.AppUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class DownloadableItemComponentLegacy extends AppComponent<DownloadableItemComponentControllerLegacy> {

    private final MultiFormatDownloadable downloadable;

    public DownloadableItemComponentLegacy(Stage stage, MultiFormatDownloadable downloadable) {
        super(stage);
        this.downloadable = downloadable;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/downloadable-item-component.fxml";
    }

    @Override
    protected DownloadableItemComponentControllerLegacy getController() {
        return new DownloadableItemComponentControllerLegacy(stage, downloadable);
    }

    @Override
    public DownloadableItemComponentControllerLegacy load() {
        DownloadableItemComponentControllerLegacy item = super.load();
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
            BaseDownloadable mp3Downloadable = new BaseDownloadable(downloadable.getBaseUrl(), downloadable.getFormatId());
            mp3Downloadable.setTitle(downloadable.getTitle());
            mp3Downloadable.setPostprocessingSteps(List.of(ExtractAudioPostprocessing.newInstance(AudioFormat.MP3.toString(), 0)));
            AppUtils.executeCommandResolvingPath(stage, new DownloadCommand(stage, mp3Downloadable), mp3Downloadable::setDownloadPath);
            e.consume();
        });
        ctxMenu.getItems().add(mp3DownloadMenuItem);

        /*boolean autodownloadEnabled = ConfigRegistry.get(AutoDownloadPref.class).getValue();
        if (autodownloadEnabled) {
            MenuItem addToQueueMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.contextmenu.queue.add"));
            addToQueueMenuItem.setOnAction(e -> {
                String format = ConfigRegistry.get(AutoDownloadFormatPref.class).getValue();
                BaseDownloadable customFormatDownloadable = new BaseDownloadable(downloadable.getBaseUrl(), format);
                customFormatDownloadable.setTitle(downloadable.getTitle());
                customFormatDownloadable.setPostprocessingSteps(downloadable.getPostprocessingSteps());
                AppUtils.executeCommandResolvingPath(stage, new EnqueueCommand(customFormatDownloadable), customFormatDownloadable::setDownloadPath);
                e.consume();
            });
            ctxMenu.getItems().add(addToQueueMenuItem);
        }*/

        return ctxMenu;
    }
}
