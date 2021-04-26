package com.github.engatec.vdl.ui.component;

import com.github.engatec.vdl.controller.components.downloadableitem.DownloadableItemComponentController;
import com.github.engatec.vdl.model.downloadable.MultiFormatDownloadable;
import javafx.stage.Stage;

public class DownloadableItemComponent extends AppComponent<DownloadableItemComponentController> {

    private final MultiFormatDownloadable downloadable;

    public DownloadableItemComponent(Stage stage, MultiFormatDownloadable downloadable) {
        super(stage);
        this.downloadable = downloadable;
    }

    @Override
    protected String getFxmlPath() {
        return "/fxml/downloadable-item.fxml";
    }

    @Override
    protected DownloadableItemComponentController getController() {
        return new DownloadableItemComponentController(downloadable);
    }
}
