package com.github.engatec.vdl.controller.components.downloadableitem;

import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import javafx.scene.layout.VBox;

public class DownloadableItemComponentController extends VBox {

    private final MultiFormatDownloadable downloadable;

    public DownloadableItemComponentController(MultiFormatDownloadable downloadable) {
        this.downloadable = downloadable;
    }
}
