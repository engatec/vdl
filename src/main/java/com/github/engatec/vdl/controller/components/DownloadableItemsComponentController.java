package com.github.engatec.vdl.controller.components;

import java.util.List;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

import com.github.engatec.vdl.core.ApplicationContext;
import com.github.engatec.vdl.core.command.EnqueueCommand;
import com.github.engatec.vdl.core.preferences.ConfigManager;
import com.github.engatec.vdl.model.downloadable.Audio;
import com.github.engatec.vdl.model.downloadable.CustomFormatDownloadable;
import com.github.engatec.vdl.model.downloadable.Video;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadConfigItem;
import com.github.engatec.vdl.model.preferences.general.AutoDownloadFormatConfigItem;
import com.github.engatec.vdl.ui.Dialogs;
import com.github.engatec.vdl.util.AppUtils;
import com.github.engatec.vdl.worker.data.DownloadableData;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DownloadableItemsComponentController {

    private Stage stage;
    private List<DownloadableData> downloadableDataList;
    private BiFunction<List<Video>, List<Audio>, ? extends Parent> contentFunction;

    @FXML private VBox rootVBox;

    private DownloadableItemsComponentController() {
    }

    public DownloadableItemsComponentController(Stage stage, List<DownloadableData> downloadableDataList, BiFunction<List<Video>, List<Audio>, ? extends Parent> contentFunction) {
        this.stage = stage;
        this.downloadableDataList = downloadableDataList;
        this.contentFunction = contentFunction;
    }

    @FXML
    public void initialize() {
        rootVBox.setSpacing(4);

        boolean autodownloadEnabled = ConfigManager.INSTANCE.getValue(new AutoDownloadConfigItem());
        boolean singleItem = downloadableDataList.size() == 1;
        for (DownloadableData item : downloadableDataList) {
            TitledPane tp = new TitledPane(item.getTitle(), contentFunction.apply(item.getVideoList(), item.getAudioList()));
            tp.setExpanded(singleItem);
            tp.setCollapsible(!singleItem);
            tp.getStyleClass().add("no-border");
            if (!singleItem && autodownloadEnabled) {
                setContextMenu(tp, item);
            }
            rootVBox.getChildren().add(tp);
        }
    }

    private void setContextMenu(TitledPane tp, DownloadableData item) {
        ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        ContextMenu ctxMenu = new ContextMenu();

        MenuItem addToQueueMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.queue.add"));
        addToQueueMenuItem.setOnAction(e -> {
            String format = ConfigManager.INSTANCE.getValue(new AutoDownloadFormatConfigItem());
            CustomFormatDownloadable downloadable = new CustomFormatDownloadable(item.getBaseUrl(), format);
            AppUtils.resolveDownloadPath(stage).ifPresentOrElse(
                    path -> {
                        downloadable.setDownloadPath(path);
                        new EnqueueCommand(downloadable).execute();
                    },
                    () -> Dialogs.error(ApplicationContext.INSTANCE.getResourceBundle().getString("download.path.directory.error"))
            );
            e.consume();
        });

        ctxMenu.getItems().addAll(addToQueueMenuItem);
        tp.setContextMenu(ctxMenu);
    }
}