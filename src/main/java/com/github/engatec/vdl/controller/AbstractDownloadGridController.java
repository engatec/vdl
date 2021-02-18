package com.github.engatec.vdl.controller;

import com.github.engatec.vdl.core.I18n;
import com.github.engatec.vdl.core.action.AddToQueueAction;
import com.github.engatec.vdl.core.action.DownloadAction;
import com.github.engatec.vdl.model.downloadable.Downloadable;
import com.github.engatec.vdl.util.ActionUtils;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public abstract class AbstractDownloadGridController extends GridPane {

    protected Button createDownloadButton(Stage parentStage, Downloadable downloadable) {
        Button downloadBtn = new Button();
        I18n.bindLocaleProperty(downloadBtn.textProperty(), "download");
        downloadBtn.setOnAction(e -> {
            ActionUtils.performActionResolvingPath(parentStage, new DownloadAction(parentStage, downloadable), downloadable::setDownloadPath);
            e.consume();
        });
        return downloadBtn;
    }

    protected Button createAddToQueueButton(Stage parentStage, Downloadable downloadable) {
        Button addToQueueBtn = new Button();
        I18n.bindLocaleProperty(addToQueueBtn.textProperty(), "component.downloadgrid.queue.add");
        addToQueueBtn.setOnAction(e -> {
            ActionUtils.performActionResolvingPath(parentStage, new AddToQueueAction(downloadable), downloadable::setDownloadPath);
            e.consume();
        });
        return addToQueueBtn;
    }
}
