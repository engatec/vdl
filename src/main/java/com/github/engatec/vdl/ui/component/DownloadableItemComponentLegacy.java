package com.github.engatec.vdl.ui.component;

import javafx.scene.control.ContextMenu;

public class DownloadableItemComponentLegacy {

    /*private final MultiFormatDownloadable downloadable;

    public DownloadableItemComponentLegacy(Stage stage, MultiFormatDownloadable downloadable) {
        this.downloadable = downloadable;
    }*/

    private ContextMenu createContextMenu() {
        /*ResourceBundle resourceBundle = ApplicationContext.INSTANCE.getResourceBundle();

        ContextMenu ctxMenu = new ContextMenu();

        MenuItem postprocessingMenuItem = new MenuItem(resourceBundle.getString("component.downloadgrid.contextmenu.postprocessing"));
        postprocessingMenuItem.setOnAction(e -> {
            new PostprocessingStage(downloadable).modal(stage).showAndWait();
            e.consume();
        });
        ctxMenu.getItems().add(postprocessingMenuItem);

        *//*boolean autodownloadEnabled = ConfigRegistry.get(AutoDownloadPref.class).getValue();
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
        }*//*

        return ctxMenu;*/
        return null;
    }
}
